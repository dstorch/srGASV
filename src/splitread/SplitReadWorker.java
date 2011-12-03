package splitread;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.samtools.SAMRecord;

public class SplitReadWorker
{
	private BAMReaderWriter m_bamreader;
	private GASVRegionReader m_grr;
	
	public SplitReadWorker(File bamfile, File gasvOutfile)
	{
		m_bamreader = new BAMReaderWriter(bamfile);
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
	
	public void processOneRegion(GASVRegion region)
	{
		// TODO remove this
		System.out.println("processing one region");
		
    	// look for left deletion breakpoint
    	for (SAMRecord record : m_bamreader.getSplitreadMates(region.getLeftChromosome(), region.getRegionX(), true))
    	{
    		Aligner aligner = new Aligner(record, region);
    		Alignment alignment = aligner.align();
    		alignment.print();
    	}
    	
    	// look for right deletion breakpoint
    	/*for (SAMRecord record : bamreader.getSplitreadCandidates(region.getRightChromosome(), region.getRegionY(), true))
    	{
    		
    	}*/
	}
	
	public void processGASVOut() throws IOException, InterruptedException
	{
		List<GASVRegion> regionList = m_grr.read();
		
		Set<String> allCandidateReads = new HashSet<String>();
		
		// get names for anchor reads
		for (GASVRegion region : regionList)
		{
			List<SAMRecord> records = m_bamreader.getSplitreadMates(region.getLeftChromosome(), region.getRegionX(), true);
			Set<String> readNames = new HashSet<String>();
			
			for (SAMRecord r : records)
			{
				readNames.add(r.getReadName());
			}
			
			region.setCandidateReads(readNames);
			
			allCandidateReads.addAll(readNames);
		}
		
		// write a BAM file with all interesting reads
		m_bamreader.writeCandidatesFile(allCandidateReads);
		
		// process each region in turn
		for (GASVRegion region : regionList)
		{
			// TODO remove this
			int leftchr = region.getLeftChromosome();
			int rightchr = region.getRightChromosome();
			int one = region.getRegionX().u;
			int two = region.getRegionX().v;
			int three = region.getRegionY().u;
			int four = region.getRegionY().v;
			System.out.println(leftchr + ":" + one + "-" + two + ", " + rightchr + ":" + three + "-" + four);
			System.out.println(region.getFragX());
			System.out.println(region.getFragY());
			
			for (SAMRecord record : m_bamreader.getSplitreadCandidates(region.getCandidateReads()))
			{
	    		Aligner aligner = new Aligner(record, region);
	    		Alignment alignment = aligner.align();
	    		System.out.println(record.getReadName());
	    		alignment.print();
			}
			
			System.out.print("\n\n");
		}
	}
}
