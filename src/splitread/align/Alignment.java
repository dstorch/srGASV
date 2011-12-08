package splitread.align;

import splitread.GASVRegion;
import net.sf.samtools.SAMRecord;

public class Alignment
{
	private final SAMRecord m_samRecord;
	private final GASVRegion m_gasvRegion;

	private final int m_score;
	private final int m_bp1;
	private final int m_bp2;
	private final int m_chr1;
	private final int m_chr2;
	private final String m_referenceAlignment;
	private final String m_readAlignment;

	private Alignment(AlignmentBuilder builder)
	{
		m_samRecord = builder.getSAMRecord();
		m_gasvRegion = builder.getGASVRegion();
		m_score = builder.getScore();
		m_bp1 = builder.getBP1();
		m_bp2 = builder.getBP2();
		m_chr1 = builder.getChromosomeLeft();
		m_chr2 = builder.getChromosomeRight();
		m_referenceAlignment = builder.getReference();
		m_readAlignment = builder.getRead();
	}

	public static AlignmentBuilder createBuilder()
	{
		return new AlignmentBuilder();
	}

	public static class AlignmentBuilder
	{
		private SAMRecord samRecord;
		private GASVRegion gasvRegion;
		
		private int score;
		private int bp1;
		private int bp2;
		private int chromosome1;
		private int chromosome2;
		private String referenceAlignment;
		private String readAlignment;

		public AlignmentBuilder()
		{
			referenceAlignment = "";
			readAlignment = "";
		}

		public AlignmentBuilder setScore(int score)
		{
			this.score = score;
			return this;
		}
		
		public AlignmentBuilder setSAMRecord(SAMRecord record)
		{
			this.samRecord = record;
			return this;
		}
		
		public SAMRecord getSAMRecord()
		{
			return this.samRecord;
		}
		
		public AlignmentBuilder setGASVRegion(GASVRegion region)
		{
			this.gasvRegion = region;
			return this;
		}
		
		public GASVRegion getGASVRegion()
		{
			return this.gasvRegion;
		}
		
		public AlignmentBuilder setBP1(int bp1, int chromosome1)
		{
			this.bp1 = bp1;
			this.chromosome1 = chromosome1;
			return this;
		}

		public AlignmentBuilder setBP2(int bp2, int chromosome2)
		{
			this.bp2 = bp2;
			this.chromosome2 = chromosome2;
			return this;
		}

		public AlignmentBuilder appendReference(char next)
		{
			this.referenceAlignment = next + this.referenceAlignment;
			return this;
		}

		public AlignmentBuilder appendRead(char next)
		{
			this.readAlignment = next + this.readAlignment;
			return this;
		}
		
		public int getScore()
		{
			return score;
		}

		public int getBP1()
		{
			return bp1;
		}

		public int getBP2()
		{
			return bp2;
		}
		
		public int getChromosomeLeft()
		{
			return chromosome1;
		}
		
		public int getChromosomeRight()
		{
			return chromosome2;
		}

		public String getReference()
		{
			return referenceAlignment;
		}

		public String getRead()
		{
			return readAlignment;
		}

		public Alignment build()
		{
			return new Alignment(this);
		}

	}

	public void print()
	{
		System.out.println("cluster name: " + m_gasvRegion.getClusterName());
		System.out.println("read name: " + m_samRecord.getReadName());
		System.out.println("GASV regions: " + m_gasvRegion);
		System.out.println("breakpoints: " + m_chr1 + ":" + m_bp1 + ", " + m_chr2 + ":" + m_bp2);
		System.out.println("alignment score: " + m_score);
		System.out.println(m_referenceAlignment);
		System.out.println(m_readAlignment);
	}
	
	public void printSimple()
	{
		System.out.println(m_referenceAlignment);
		System.out.println(m_readAlignment);
	}

	public int getScore()
	{
		return m_score;
	}
	
	public int getBP1()
	{
		return m_bp1;
	}

	public int getBP2()
	{
		return m_bp2;
	}

	public String getReference()
	{
		return m_referenceAlignment;
	}

	public String readAlignment()
	{
		return m_readAlignment;
	}
}