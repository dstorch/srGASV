package splitread;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMFileWriterFactory;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

// see javadoc: http://picard.sourceforge.net/javadoc/
// for documentation of picard API for reading BAM files
//
// TODO how to only search within a certain index
// TODO how to group mate pairs
public class BAMReaderWriter
{
	private File m_bamfile;
	private File m_bamfileProcessed;
	private SAMFileReader m_reader;
	private SAMFileReader m_readerProcessed;

	public BAMReaderWriter(File bamfile)
	{
		m_bamfile = bamfile;
		m_bamfileProcessed = new File(bamfile.getAbsolutePath() + Constants.BAM_EXTENSION);
		
		if (m_bamfileProcessed.exists())
		{
			m_readerProcessed = new SAMFileReader(m_bamfileProcessed);
			m_readerProcessed.setValidationStringency(SAMFileReader.ValidationStringency.SILENT);
		}
		
		m_reader = new SAMFileReader(m_bamfile);
		m_reader.setValidationStringency(SAMFileReader.ValidationStringency.SILENT);
	}

	// TODO make the criteria for picking candidates are sound
	public List<SAMRecord> getSplitreadMates(Integer chromosome, Point region, boolean left)
	{
		List<SAMRecord> candidates = new LinkedList<SAMRecord>();

		SAMRecordIterator it;
		if (left)
		{
			it = m_reader.query(chromosome.toString(), region.u - Constants.FRAG_LENGTH_MAX, region.u, false);
		}
		else
		{
			it = m_reader.query(chromosome.toString(), region.v + Constants.FRAG_LENGTH_MAX, region.v, false);
		}

		SAMRecord curRecord;
		while (it.hasNext())
		{
			curRecord = it.next();
			
			int mapq = curRecord.getMappingQuality();
			boolean hasMate = curRecord.getReadPairedFlag();
			boolean mateUnmapped = curRecord.getMateUnmappedFlag();

			if (hasMate && mateUnmapped && mapq >= Constants.MIN_MAPQ)
			{
				candidates.add(curRecord);
			}
		}

		// close the iterator once iteration is complete
		it.close();

		return candidates;
	}
	
	public void writeCandidatesFile(Set<String> candidates)
	{
		if (m_bamfileProcessed.exists()) return;
		
		SAMFileWriter outputSam = new SAMFileWriterFactory().makeSAMOrBAMWriter(m_reader.getFileHeader(), true, m_bamfileProcessed);
		
		SAMRecordIterator it = m_reader.iterator();
		SAMRecord curRecord;
		while (it.hasNext())
		{
			curRecord = it.next();
			if (candidates.contains(curRecord.getReadName()))
			{
				outputSam.addAlignment(curRecord);
			}
		}
		it.close();
		outputSam.close();
		
		m_readerProcessed = new SAMFileReader(m_bamfileProcessed);
		m_readerProcessed.setValidationStringency(SAMFileReader.ValidationStringency.SILENT);
	}
	
	public List<SAMRecord> getSplitreadCandidates(Set<String> names)
	{
		List<SAMRecord> outlist = new LinkedList<SAMRecord>();
		
		SAMRecordIterator it = m_readerProcessed.iterator();
		SAMRecord curRecord;
		while (it.hasNext())
		{
			curRecord = it.next();
			
			if (names.contains(curRecord.getReadName()))
			{
				boolean unmapped = curRecord.getReadUnmappedFlag();
				if (unmapped) outlist.add(curRecord);
			}
		}
		it.close();
		
		return outlist;
	}

}
