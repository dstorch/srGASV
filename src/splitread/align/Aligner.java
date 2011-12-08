package splitread.align;

import splitread.GASVRegion;
import splitread.SplitReadException;
import splitread.SplitReadWorker;

import net.sf.samtools.SAMRecord;

public abstract class Aligner
{
	protected SAMRecord m_samRecord;
	protected GASVRegion m_gasvRegion;

	protected int[][] m_tableA;
	protected int[][] m_tableB;
	protected char[] m_read;
	protected char[] m_region1;
	protected char[] m_region2;
	protected int[] m_tableMins;
	protected int[] m_minLocations;

	protected Alignment.AlignmentBuilder m_builder;
	
	// aligner factory
	public static Aligner create(SAMRecord record, GASVRegion region, boolean left) throws SplitReadException
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

	public Aligner(SAMRecord record, GASVRegion region)
	{
		m_samRecord = record;
		m_gasvRegion = region;

		m_read = SplitReadWorker.toCharArray(record.getReadBases());
		m_region1 = region.getFragX();
		m_region2 = region.getFragY();

		m_tableA = new int[m_read.length + 1][m_region1.length + 1];
		m_tableB = new int[m_read.length + 1][m_region2.length + 1];

		m_tableMins = new int[m_read.length + 1];
		m_minLocations = new int[m_read.length + 1];

		m_builder = Alignment.createBuilder();
		
		m_builder.setGASVRegion(m_gasvRegion).setSAMRecord(m_samRecord);
	
	}
	
	// TODO dummy aligner for testing
	public Aligner(char[] read, char[] fragment1, char[] fragment2, GASVRegion dummy)
	{
		m_read = read;
		m_region1 = fragment1;
		m_region2 = fragment2;
		
		m_gasvRegion = dummy;

		m_tableA = new int[m_read.length + 1][m_region1.length + 1];
		m_tableB = new int[m_read.length + 1][m_region2.length + 1];

		m_tableMins = new int[m_read.length + 1];
		m_minLocations = new int[m_read.length + 1];

		m_builder = Alignment.createBuilder();
		
	}

	public abstract Alignment align();

}