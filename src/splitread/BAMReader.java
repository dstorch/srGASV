package splitread;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

// see javadoc: http://picard.sourceforge.net/javadoc/
// for documentation of picard API for reading BAM files
//
// TODO how to only search within a certain index
// TODO how to group mate pairs
public class BAMReader
{
	private SAMFileReader m_reader;

	public BAMReader(File bamfile) throws SplitReadException
	{
		m_reader = new SAMFileReader(bamfile);
		m_reader.setValidationStringency(SAMFileReader.ValidationStringency.SILENT);
		
		if (!m_reader.hasIndex())
		{
			throw new SplitReadException("BAM file has no index");
		}
	}

	// TODO make the criteria for picking candidates are sound
	public Set<String> getSplitreadMates(Integer chromosome, Point region, boolean left)
	{
		Set<String> candidates = new HashSet<String>();

		SAMRecordIterator it;
		if (left)
		{
			it = m_reader.query(chromosome.toString(), region.u - Constants.FRAG_LENGTH_MAX, region.u, false);
		}
		else
		{
			it = m_reader.query(chromosome.toString(), region.v, region.v + Constants.FRAG_LENGTH_MAX, false);
		}

		SAMRecord curRecord;
		while (it.hasNext())
		{	
			curRecord = it.next();
			boolean hasMate = curRecord.getReadPairedFlag();
			
			if (hasMate)
			{
				boolean mateUnmapped = curRecord.getMateUnmappedFlag();
				int mapq = curRecord.getMappingQuality();
				
				boolean oriented = curRecord.getReadNegativeStrandFlag();
				if (left) oriented = !oriented;
				
				if (mateUnmapped && oriented && mapq >= Constants.MIN_MAPQ)
				{	
					candidates.add(curRecord.getReadName());
				}
			}
			

		}

		// close the iterator once iteration is complete
		it.close();

		return candidates;
	}

	public List<SAMRecord> getSplitreadCandidates(Integer chromosome, Point region, boolean left, Set<String> mates)
	{
		List<SAMRecord> candidates = new LinkedList<SAMRecord>();

		SAMRecordIterator it;
		if (left)
		{
			it = m_reader.query(chromosome.toString(), region.u - Constants.FRAG_LENGTH_MAX, region.u, false);
		}
		else
		{
			it = m_reader.query(chromosome.toString(), region.v, region.v + Constants.FRAG_LENGTH_MAX, false);
		}

		SAMRecord curRecord;
		while (it.hasNext())
		{
			curRecord = it.next();
			boolean unmapped = curRecord.getReadUnmappedFlag();
			if (unmapped && mates.contains(curRecord.getReadName()))
			{
				candidates.add(curRecord);
			}
		}

		// close the iterator once iteration is complete
		it.close();

		return candidates;
	}
	
	public void close()
	{
		m_reader.close();
	}

}
