package splitread.align;

import splitread.GASVCluster;
import splitread.Read;
import splitread.SplitReadException;

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
	protected Read m_read;
	protected GASVCluster m_gasvRegion;

	protected int[][] m_tableA;
	protected int[][] m_tableB;
	protected char[] m_seq;
	protected char[] m_region1;
	protected char[] m_region2;
	protected int[] m_tableMins;
	protected int[] m_minLocations;

	protected Alignment.AlignmentBuilder m_builder;
	
	// aligner factory
	public static Aligner create(Read read, GASVCluster region, boolean left) throws SplitReadException
	{
		switch(region.getSVType())
		{
		case DELETION:
			return new DeletionAligner(read, region);
		case INVERSION:
			return new InversionAligner(read, region, left);
		default:
			throw new SplitReadException("invalid sv type");
		}
	}

	public Aligner(Read read, GASVCluster region)
	{
		m_read = read;
		m_gasvRegion = region;

		m_seq = read.getSequence().toCharArray();
		m_region1 = region.getFragX();
		m_region2 = region.getFragY();

		m_tableA = new int[m_seq.length + 1][m_region1.length + 1];
		m_tableB = new int[m_seq.length + 1][m_region2.length + 1];

		m_tableMins = new int[m_seq.length + 1];
		m_minLocations = new int[m_seq.length + 1];

		m_builder = Alignment.createBuilder();
		
		m_builder.setGASVRegion(m_gasvRegion).setRead(m_read);
	
	}

	public abstract Alignment align();

}