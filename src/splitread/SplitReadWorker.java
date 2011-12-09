package splitread;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
	private BAMReader m_bamreader;
	private BAMReader m_unmappedBamreader = null;
	private GASVClusterReader m_grr;
	
	public SplitReadWorker(File bamfile, File gasvOutfile, File unmappedFile) throws SplitReadException
	{
		m_bamreader = new BAMReader(bamfile);
		m_grr = new GASVClusterReader(gasvOutfile);
		
		if (unmappedFile.exists() && unmappedFile.isFile())
		{
			m_unmappedBamreader = new BAMReader(unmappedFile);
		}
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
		
		Set<SAMRecord> mates = m_bamreader.getSplitreadMates(region.getLeftChromosome(), location, left);
		
		List<SAMRecord> splits;
		if (m_unmappedBamreader != null)
		{
			splits = m_unmappedBamreader.getSplitreadCandidates(region.getLeftChromosome(), location, left, mates);
		}
		else
		{
			splits = m_bamreader.getSplitreadCandidates(region.getLeftChromosome(), location, left, mates);
		}
		
		// the number of mates should always equal the number of candidate split reads
		//if (mates.size() != splits.size()) throw new SplitReadException("matching mates not found");
		
    	for (SAMRecord record : splits)
    	{
    		Aligner aligner = Aligner.create(record, region, left);
    		Alignment alignment = aligner.align();
    		
    		// only print if the alignment distance is under the threshold
    		if (alignment.getScore() > Constants.MAX_ALIGNMENT_DIST) return;
    		
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
		// make sure that regions are correctly arranged
		/*if (region.getRegionX().v >= region.getRegionY().u)
		{
			System.err.println("invalid candidate read configuration");
			return;
		}*/
		
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
			m_bamreader.close();
			
			if (m_unmappedBamreader != null) m_unmappedBamreader.close();
		}
		catch (IOException e) {}
	}
}
