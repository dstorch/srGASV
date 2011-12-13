package splitread.io;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import splitread.Constants;
import splitread.Point;
import splitread.SplitReadException;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

/**
 * Wrapper for Picard's SAMFileReader which allows
 * candidate split reads to be obtained.
 * 
 * @author dstorch@cs.brown.edu
 * @since December 2011
 */
public class BAMReader
{
	private SAMFileReader m_reader;
	
	private boolean m_builtmap;
	private Map<String, SAMRecord> m_reads;

	public BAMReader(File bamfile)
	{
		m_reader = new SAMFileReader(bamfile);
		m_reader.setValidationStringency(SAMFileReader.ValidationStringency.SILENT);
		
		m_builtmap = false;
		m_reads = new HashMap<String, SAMRecord>();
	}

	// TODO make the criteria for picking candidates are sound
	public Set<SAMRecord> getSplitreadMates(Integer chromosome, Point region, boolean left) throws SplitReadException
	{
		if (!m_reader.hasIndex())
		{
			throw new SplitReadException("BAM file has no index");
		}
		
		Set<SAMRecord> candidates = new HashSet<SAMRecord>();

		SAMRecordIterator it;
		if (left)
		{
			it = m_reader.query(Constants.CHR_PREFIX + chromosome.toString(), region.u - Constants.FRAG_LENGTH_MAX,
					region.v - Constants.FRAG_LENGTH_MIN, false);
		}
		else
		{
			it = m_reader.query(Constants.CHR_PREFIX + chromosome.toString(), region.u + Constants.FRAG_LENGTH_MIN,
					region.v + Constants.FRAG_LENGTH_MAX, false);
		}

		SAMRecord curRecord;
		while (it.hasNext())
		{	
			curRecord = it.next();
			boolean hasMate = curRecord.getReadPairedFlag();
			
			if (hasMate)
			{
				// TODO account for mapping quality
				//int mapq = curRecord.getMappingQuality();
				
				boolean oriented = curRecord.getReadNegativeStrandFlag();
				if (left) oriented = !oriented;
				
				if (oriented /*&& mapq >= Constants.MIN_MAPQ*/)
				{	
					candidates.add(curRecord);
				}
			}
		}

		// close the iterator once iteration is complete
		it.close();

		return candidates;
	}

	public List<SAMRecord> getSplitreadCandidates(Integer chromosome, Point region, boolean left, Set<SAMRecord> mates)
	{
		/*if (!m_builtmap)
		{
			SAMRecordIterator it = m_reader.iterator();

			SAMRecord curRecord;
			while (it.hasNext())
			{
				curRecord = it.next();
				m_reads.put(curRecord.getReadName(), curRecord);
			}
			
			it.close();
			
			m_builtmap = true;
		}
		
		List<SAMRecord> candidates = new LinkedList<SAMRecord>();
		
		Set<String> nameset = new HashSet<String>();
		Set<String> seqset = new HashSet<String>();
		for (SAMRecord record : mates)
		{
			nameset.add(record.getReadName());
		}
		
		for (String mateName : nameset)
		{
			
			if (m_reads.containsKey(mateName))
			{
				SAMRecord record = m_reads.get(mateName);
				
				boolean unmapped = record.getReadUnmappedFlag();
				int mapq = record.getMappingQuality();
				boolean poorlyMapping = (unmapped || mapq < Constants.MIN_MAPQ);
				
				boolean nonRedundant = !seqset.contains(record.getReadString());
				
				if (poorlyMapping && nonRedundant)
				{
					seqset.add(record.getReadString());
					candidates.add(record);
				}
			}
		}
		
		return candidates;*/
		
		List<SAMRecord> candidates = new LinkedList<SAMRecord>();
		
		Set<String> nameset = new HashSet<String>();
		Set<String> seqset = new HashSet<String>();
		for (SAMRecord record : mates)
		{
			nameset.add(record.getReadName());
		}

		SAMRecordIterator it = m_reader.iterator();

		SAMRecord curRecord;
		while (it.hasNext())
		{
			curRecord = it.next();
			boolean unmapped = curRecord.getReadUnmappedFlag();
			int mapq = curRecord.getMappingQuality();
			boolean poorlyMapping = (unmapped || mapq < Constants.MIN_MAPQ);
			
			boolean isCandidate = nameset.contains(curRecord.getReadName());
			boolean nonRedundant = !seqset.contains(curRecord.getReadString());
			
			if (poorlyMapping && isCandidate && nonRedundant)
			{
				seqset.add(curRecord.getReadString());
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
