package splitread.align;

import splitread.GASVCluster;
import splitread.SplitReadException;
import splitread.Utils;

import net.sf.samtools.SAMRecord;

/**
 * Abstract class for the alignment routine.
 * This class should be extended by concrete
 * implementation classes for each type of
 * structural variant.
 * 
 * Each subclass must implement the abstract
 * method align().
 * 
 * @author dstorch@cs.brown.edu
 * @since December 2011
 */
public abstract class Aligner
{
	protected SAMRecord m_samRecord;
	protected GASVCluster m_gasvRegion;

	protected int[][] m_tableA;
	protected int[][] m_tableB;
	protected char[] m_read;
	protected char[] m_region1;
	protected char[] m_region2;
	protected int[] m_tableMins;
	protected int[] m_minLocations;

	protected Alignment.AlignmentBuilder m_builder;
	
	// aligner factory
	public static Aligner create(SAMRecord record, GASVCluster region, boolean left) throws SplitReadException
	{
		switch(region.getSVType())
		{
		case DELETION:
			return new DeletionAligner(record, region);
		case INVERSION:
			return new InversionAligner(record, region, left);
		default:
			throw new SplitReadException("invalid sv type");
		}
	}

	public Aligner(SAMRecord record, GASVCluster region)
	{
		m_samRecord = record;
		m_gasvRegion = region;

		m_read = Utils.toCharArray(record.getReadBases());
		m_region1 = region.getFragX();
		m_region2 = region.getFragY();

		m_tableA = new int[m_read.length + 1][m_region1.length + 1];
		m_tableB = new int[m_read.length + 1][m_region2.length + 1];

		m_tableMins = new int[m_read.length + 1];
		m_minLocations = new int[m_read.length + 1];

		m_builder = Alignment.createBuilder();
		
		m_builder.setGASVRegion(m_gasvRegion).setSAMRecord(m_samRecord);
	
	}

	public abstract Alignment align();

}