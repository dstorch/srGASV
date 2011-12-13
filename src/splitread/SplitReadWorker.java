package splitread;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import splitread.align.Aligner;
import splitread.align.Alignment;
import splitread.io.BAMReader;
import splitread.io.GASVClusterReader;

import net.sf.samtools.SAMRecord;

/**
 * Includes high-level functions for looping through
 * the input files.
 * 
 * @author dstorch@cs.brown.edu
 * @since December 2011
 */
public class SplitReadWorker
{
	// readers for input files
	private BAMReader m_mapped;
	private BAMReader m_unmapped;
	private GASVClusterReader m_grr;
	
	// majority vote breakpoint reporting
	private Map<Integer, Integer> m_bp1hist;
	private Map<Integer, Integer> m_bp2hist;
	
	public SplitReadWorker(File mapped, File gasvOutfile, File unmapped) throws SplitReadException
	{
		m_mapped = new BAMReader(mapped);
		m_unmapped = new BAMReader(unmapped);
		m_grr = new GASVClusterReader(gasvOutfile);
		
		m_bp1hist = new LinkedHashMap<Integer, Integer>();
		m_bp2hist = new LinkedHashMap<Integer, Integer>();
	}
	
	public static void stopWorking(String message)
	{
		System.err.println(message);
		System.exit(1);
	}
	
	/**
	 * Process candidate split reads from one side of the SV event
	 * 
	 * @param region - represents the "bounding box" of a GASV cluster
	 * @param left - if true, then get candidates from the left, otherwise
	 *    get candidates from the right
	 * 
	 * @throws SplitReadException
	 */
	private void oneSideSplitreads(GASVCluster region, boolean left) throws SplitReadException
	{
		Point location;
		if (left) location = region.getRegionX();
		else location = region.getRegionY();
		
		Set<SAMRecord> mates = m_mapped.getSplitreadMates(region.getLeftChromosome(), location, left);
		
		List<SAMRecord> splits = m_unmapped.getSplitreadCandidates(region.getLeftChromosome(), location, left, mates);
		
		// TODO
		System.out.println("done reading in the unmapped reads");
		
    	for (SAMRecord record : splits)
    	{
    		Aligner aligner = Aligner.create(record, region, left);
    		Alignment alignment = aligner.align();
    		
    		// only print if the alignment distance is under the threshold
    		if (alignment.getScore() > Constants.MAX_ALIGNMENT_DIST) continue;
    		
    		// ignore if breakpoints are zeros
    		if (alignment.getBP1() == 0 || alignment.getBP2() == 0) continue;
    		
    		// skip reads whose alignment is not centered over the break
    		if (alignment.getLeftChars() < Constants.MIN_PER_SIDE) continue;
    		if (alignment.getRightChars() < Constants.MIN_PER_SIDE) continue;
    		
    		// keep track of bp1 votes
    		int bp1 = alignment.getBP1();
    		if (m_bp1hist.containsKey(bp1))
    		{
    			int count = m_bp1hist.get(bp1);
    			count++;
    			m_bp1hist.put(bp1, count);
    		}
    		else
    		{
    			m_bp1hist.put(bp1, 1);
    		}
    		
    		// keep track of bp2 votes
    		int bp2 = alignment.getBP2();
    		if (m_bp2hist.containsKey(bp2))
    		{
    			int count = m_bp2hist.get(bp2);
    			count++;
    			m_bp2hist.put(bp2, count);
    		}
    		else
    		{
    			m_bp2hist.put(bp2, 1);
    		}
    		
    		// print to output stream
    		switch(Constants.OUTPUT_FORMAT)
    		{
    		case TABULAR:
    			alignment.printTabular();
    			break;
    		case VERBOSE:
    			alignment.printVerbose();
    			break;
    		}
    	}
	}
	
	public void processOneRegion(GASVCluster region) throws SplitReadException
	{
		if (Constants.OUTPUT_FORMAT == Constants.OutputFormat.VERBOSE)
		{
			Constants.OUTPUT_STREAM.println(region);
			Constants.OUTPUT_STREAM.println(region.getFragX());
			Constants.OUTPUT_STREAM.println(region.getFragY());
			Constants.OUTPUT_STREAM.println("left region: " + (region.getRegionX().u - Constants.FRAG_LENGTH_MAX) + "-" + region.getRegionX().u);
		}
		
		// get reads on the left side
		oneSideSplitreads(region, true);
		
		if (Constants.OUTPUT_FORMAT == Constants.OutputFormat.VERBOSE)
		{
			Constants.OUTPUT_STREAM.println("right region: " + region.getRegionY().v + "-" + (region.getRegionY().v + Constants.FRAG_LENGTH_MAX));
		}
		
		// get reads on the right side
		oneSideSplitreads(region, false);
		
		if (Constants.OUTPUT_FORMAT == Constants.OutputFormat.VERBOSE)
		{
			Constants.OUTPUT_STREAM.println("\n\n=====================\n");
		}
		
		if (Constants.OUTPUT_FORMAT == Constants.OutputFormat.CONCISE)
		{
			printConcise(region);
		}
	}
	
	private void printConcise(GASVCluster cluster)
	{
		// find the best breakpoints and the number of supporting reads
		int bp1Best = -1;
		int bp1Votes = -1;
		for (Integer bp1 : m_bp1hist.keySet())
		{
			int count = m_bp1hist.get(bp1);
			if (count > bp1Votes)
			{
				bp1Best = bp1;
				bp1Votes = count;
			}
		}
		
		int bp2Best = -1;
		int bp2Votes = -1;
		for (Integer bp2 : m_bp2hist.keySet())
		{
			int count = m_bp2hist.get(bp2);
			if (count > bp2Votes)
			{
				bp2Best = bp2;
				bp2Votes = count;
			}
		}
		
		// print single output line for cluster
		Constants.OUTPUT_STREAM.printf("%s\t%s,%s\t%d\t%d\t%d\t%d\n", cluster.getClusterName(), cluster.getRegionX(),
									   cluster.getRegionY(), bp1Best, bp2Best, bp1Votes, bp2Votes);
	}
	
	/**!
	 * Read the line for each GASV cluster, and align candidate
	 * split reads for each.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SplitReadException
	 */
	public void processGASVOut() throws IOException, InterruptedException, SplitReadException
	{
		m_grr.read(this);
	}
	
	public void cleanup()
	{
		try
		{
			Constants.OUTPUT_STREAM.close();
			m_grr.close();
			m_mapped.close();
			m_unmapped.close();
		}
		catch (IOException e) {}
	}
	
}
