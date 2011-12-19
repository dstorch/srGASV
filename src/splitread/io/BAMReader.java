package splitread.io;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import splitread.Constants;
import splitread.Point;
import splitread.Read;
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

	// database connection
	boolean m_hasDB;
	private Connection m_connection = null;
	private Statement m_statement = null;
	private ResultSet m_resultSet = null;

	public BAMReader(File bamfile)
	{
		m_reader = new SAMFileReader(bamfile);
		m_reader.setValidationStringency(SAMFileReader.ValidationStringency.SILENT);

		File dbfile = new File(bamfile.getAbsolutePath() + ".db");
		m_hasDB = dbfile.exists() && dbfile.isFile();
		if (m_hasDB)
		{
			try
			{
				Class.forName("org.sqlite.JDBC");
				m_connection = DriverManager.getConnection("jdbc:sqlite:" + dbfile.getAbsolutePath());
				m_statement = m_connection.createStatement();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				m_hasDB = false;
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
				m_hasDB = false;
			}
		}
	}

	public Set<SAMRecord> getSplitreadMates(Integer chromosome, Point region, boolean left) throws SplitReadException
	{
		if (!m_reader.hasIndex())
		{
			throw new SplitReadException("mapped BAM file has no index");
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
				int mapq = curRecord.getMappingQuality();

				boolean oriented = curRecord.getReadNegativeStrandFlag();
				if (left) oriented = !oriented;

				if (oriented && mapq >= Constants.MIN_MAPQ)
				{	
					candidates.add(curRecord);
				}
			}
		}

		// close the iterator once iteration is complete
		it.close();

		return candidates;
	}

	public List<Read> getSplitreadCandidates(Integer chromosome, Point region, boolean left, Set<SAMRecord> mates) throws ClassNotFoundException
	{
		if (m_hasDB)
		{
			return getReadsFromDB(mates);
		}

		List<Read> candidates = new LinkedList<Read>();

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

				Read read = new Read(curRecord.getReadName(), curRecord.getReadString());
				candidates.add(read);
			}
		}

		// close the iterator once iteration is complete
		it.close();

		return candidates;
	}

	public List<Read> getReadsFromDB(Set<SAMRecord> mates) throws ClassNotFoundException
	{	
		List<Read> candidates = new LinkedList<Read>();
		Set<String> seqset = new HashSet<String>();

		for (SAMRecord mate : mates)
		{
			String readName = mate.getReadName();
			String anchorSeq = mate.getReadString();
			String sql = "select * from bamfile where name = '" + readName + "';";

			try
			{
				m_resultSet = m_statement.executeQuery(sql);

				while (m_resultSet.next())
				{
					String sequence = m_resultSet.getString("sequence");
					boolean nonRedundant = !seqset.contains(sequence);
					boolean isSplit = !sequence.equals(anchorSeq);

					if (nonRedundant && isSplit)
					{
						Read read = new Read(readName, sequence);
						candidates.add(read);
					}
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				m_hasDB = false;
			}
		}

		return candidates;
	}

	public void close()
	{	
		try
		{
			m_reader.close();
			m_resultSet.close();
			m_statement.close();
			m_connection.close();
		}
		catch(Exception e) {}
	}

}
