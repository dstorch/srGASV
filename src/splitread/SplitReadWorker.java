package splitread;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.sf.samtools.SAMRecord;

public class SplitReadWorker
{
	private BAMReader m_bamreader;
	private GASVRegionReader m_grr;
	
	public SplitReadWorker(File bamfile, File gasvOutfile) throws SplitReadException
	{
		m_bamreader = new BAMReader(bamfile);
		m_grr = new GASVRegionReader(gasvOutfile);
	}
	
	public static void stopWorking(String message)
	{
		System.err.println(message);
		System.exit(1);
	}
	
	public static char[] toCharArray(byte[] bytes)
	{
		char[] chars = new char[bytes.length];
		for (int i = 0; i < bytes.length; i++)
		{
			chars[i] = (char) bytes[i];
		}
		
		return chars;
	}
	
	public void oneSideSplitreads(GASVRegion region, boolean left)
	{
		Point location;
		if (left) location = region.getRegionX();
		else location = region.getRegionY();
		
		Set<String> mates = m_bamreader.getSplitreadMates(region.getLeftChromosome(), location, left);
		
		List<SAMRecord> splits = m_bamreader.getSplitreadCandidates(region.getLeftChromosome(), location, left, mates);
		
		// the number of mates should always equal the number of candidate split reads
		assert(mates.size() == splits.size());
		
    	for (SAMRecord record : splits)
    	{
    		Aligner aligner = new Aligner(record, region);
    		Alignment alignment = aligner.align();
        	alignment.print();
    	}
	}
	
	public void processOneRegion(GASVRegion region)
	{
		System.out.println(region);
		System.out.println(region.getFragX());
		System.out.println(region.getFragY());
		
		// TODO remove this
		System.out.println("left region");
		System.out.println((region.getRegionX().u - Constants.FRAG_LENGTH_MAX) + "-" + region.getRegionX().u);
		oneSideSplitreads(region, true);
		// TODO remove this
		System.out.println("right region");
		System.out.println(region.getRegionY().v + "-" + (region.getRegionY().v + Constants.FRAG_LENGTH_MAX));
		oneSideSplitreads(region, false);
		System.out.print("\n\n\n=====================\n");
	}
	
	public void processGASVOut() throws IOException, InterruptedException
	{
		m_grr.read(this);
	}
	
	public void cleanup()
	{
		try
		{
			m_grr.close();
			m_bamreader.close();
		}
		catch (IOException e) {}
	}
}
