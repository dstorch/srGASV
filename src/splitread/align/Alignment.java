package splitread.align;

import splitread.Constants;
import splitread.GASVCluster;
import splitread.Read;

/**
 * An Alignment object is produced as the
 * output of each Aligner subclass's
 * align() method.
 * 
 * The alignment object is immutable, and therefore
 * must be constructed via the AlignmentBuilder
 * inner class.
 * 
 * @author dstorch@cs.brown.edu
 * @since December 2011
 */
public class Alignment
{
	private final Read m_read;
	private final GASVCluster m_gasvRegion;

	private final int m_score;
	private final int m_bp1;
	private final int m_bp2;
	private final int m_chr1;
	private final int m_chr2;
	private final String m_referenceAlignment;
	private final String m_readAlignment;
	private final String m_readSplitSeq;

	private Alignment(AlignmentBuilder builder)
	{
		m_read = builder.getRead();
		m_gasvRegion = builder.getGASVRegion();
		m_score = builder.getScore();
		m_bp1 = builder.getBP1();
		m_bp2 = builder.getBP2();
		m_chr1 = builder.getChromosomeLeft();
		m_chr2 = builder.getChromosomeRight();
		m_referenceAlignment = builder.getReference();
		m_readAlignment = builder.getReadAlignment();
		m_readSplitSeq = builder.getReadSplitSeq();
	}

	/**
	 * @return an AlignmentBuilder to populate with
	 * data during alignment
	 */
	public static AlignmentBuilder createBuilder()
	{
		return new AlignmentBuilder();
	}

	/**
	 * Inner class for constructing alignment objects.
	 */
	public static class AlignmentBuilder
	{
		private Read read;
		private GASVCluster gasvRegion;
		
		private int score;
		private int bp1;
		private int bp2;
		private int chromosome1;
		private int chromosome2;
		private String referenceAlignment;
		private String readAlignment;
		private String readSplitSeq;

		public AlignmentBuilder()
		{
			referenceAlignment = "";
			readAlignment = "";
			readSplitSeq = "";
		}

		public AlignmentBuilder setScore(int score)
		{
			this.score = score;
			return this;
		}
		
		public AlignmentBuilder setRead(Read read)
		{
			this.read = read;
			return this;
		}
		
		public Read getRead()
		{
			return this.read;
		}
		
		public AlignmentBuilder setGASVRegion(GASVCluster region)
		{
			this.gasvRegion = region;
			return this;
		}
		
		public GASVCluster getGASVRegion()
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
			
			if (next != '-')
			{
				this.readSplitSeq = next + this.readSplitSeq;
			}
			
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
		
		public Alignment build()
		{
			return new Alignment(this);
		}
		
		public void setReadAlignment(String readAlignment)
		{
			this.readAlignment = readAlignment;
		}
		
		public void setReferenceAlignment(String referenceAlignment)
		{
			this.referenceAlignment = referenceAlignment;
		}
		
		public String getReadAlignment()
		{
			return this.readAlignment;
		}
		
		public String getReferenceAlignment()
		{
			return this.referenceAlignment;
		}
		
		public String getReadSplitSeq()
		{
			return this.readSplitSeq;
		}

	}

	public void printTabular()
	{
		Constants.OUTPUT_STREAM.printf("%s\t%s\t%s\t%d:%d, %d:%d\t%d\t%s\n", m_gasvRegion.getClusterName(),
				  						m_read.getName(), m_gasvRegion, m_chr1, m_bp1,
										m_chr2, m_bp2, m_score, m_readSplitSeq);
	}
	
	public void printVerbose()
	{
		Constants.OUTPUT_STREAM.println("cluster name: " + m_gasvRegion.getClusterName());
		Constants.OUTPUT_STREAM.println("read name: " + m_read.getName());
		Constants.OUTPUT_STREAM.println("GASV regions: " + m_gasvRegion);
		Constants.OUTPUT_STREAM.println("breakpoints: " + m_chr1 + ":" + m_bp1 + ", " + m_chr2 + ":" + m_bp2);
		Constants.OUTPUT_STREAM.println("alignment score: " + m_score);
		Constants.OUTPUT_STREAM.println(m_referenceAlignment);
		Constants.OUTPUT_STREAM.println(m_readAlignment);
	}
	
	public void printSimple()
	{
		Constants.OUTPUT_STREAM.println("alignent score: " + m_score);
		Constants.OUTPUT_STREAM.println(m_referenceAlignment);
		Constants.OUTPUT_STREAM.println(m_readAlignment);
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
	
	public String getReadSplitSeq()
	{
		return m_readSplitSeq;
	}
	
	// number of characters left of the split
	public int getLeftChars()
	{
		return m_readSplitSeq.indexOf("|");
	}
	
	// number of characters right of the split
	public int getRightChars()
	{
		int total = m_readSplitSeq.length() - 1;
		int left = m_readSplitSeq.indexOf("|");
		return total - left;
	}
}