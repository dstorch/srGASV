package splitread;

import java.lang.Math;

import net.sf.samtools.SAMRecord;

public class Aligner
{
	private SAMRecord m_samRecord;
	private GASVRegion m_gasvRegion;

	private int[][] m_tableA;
	private int[][] m_tableB;
	private char[] m_read;
	private char[] m_region1;
	private char[] m_region2;
	private int[] m_tableMins;
	private int[] m_minLocations;

	private Alignment.AlignmentBuilder m_builder;

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
	}

	public Alignment align()
	{
		fillTableA();
		fillTableB();
		return traceback();
	}

	private void fillTableA()
	{
		// initialize first row and column
		for (int j = 0; j <= m_region1.length; j++)
		{
			m_tableA[0][j] = 0;
		}
		for (int i = 1; i <= m_read.length; i++)
		{
			m_tableA[i][0] = i * Constants.GAP;
		}

		// fill table a
		for (int i = 1; i <= m_read.length; i++)
		{
			m_tableMins[i] = Integer.MAX_VALUE;

			for (int j = 1; j <= m_region1.length; j++)
			{
				m_tableA[i][j] = Math.min(m_tableA[i-1][j] + Constants.GAP,
						Math.min(m_tableA[i][j-1] + Constants.GAP,
								m_tableA[i-1][j-1] + Constants.matchScore(m_read[i-1], m_region1[j-1])));

				if (m_tableA[i][j] < m_tableMins[i])
				{
					m_tableMins[i] = m_tableA[i][j];
					m_minLocations[i] = j;
				}
			}
		}
	}

	private void fillTableB()
	{
		// initialize table b
		for (int j = 0; j <= m_region2.length; j++)
		{
			m_tableB[0][j] = 0;
		}
		for (int i = 1; i <= m_read.length; i++)
		{
			m_tableB[i][0] = i * Constants.GAP;
		}

		// fill table b
		for (int i = 1; i <= m_read.length; i++)
		{
			for (int j = 1; j <= m_region2.length; j++)
			{
				m_tableB[i][j] = Math.min(m_tableB[i-1][j] + Constants.GAP,
						Math.min(m_tableB[i][j-1] + Constants.GAP,
								Math.min(m_tableB[i-1][j-1] + Constants.matchScore(m_read[i-1], m_region2[j-1]),
										m_tableMins[i-1] + Constants.matchScore(m_read[i-1], m_region2[j-1]))));
			}
		}
	}

	private Alignment traceback()
	{   
		// find the min in the last row of table b
		int minVal = Integer.MAX_VALUE;
		int minPos = -1;
		int curI = m_read.length;
		for (int j = 0; j <= m_region2.length; j++) {
			if (m_tableB[curI][j] < minVal)
			{
				minVal = m_tableB[curI][j];
				minPos = j;
			}
		}

		m_builder.setScore(minVal);

		// put mismatches on the end of alignment strings
		for (int counter = m_region2.length - 1; counter > minVal; counter--)
		{
			m_builder.appendRead('-');
			m_builder.appendReference(m_region2[counter]);
		}

		boolean tableB = true;
		int curJ = minPos;
		while (curI > 0)
		{
			if (tableB)
			{
				if (m_tableB[curI-1][curJ] + Constants.GAP == m_tableB[curI][curJ])
				{
					curI--;
					m_builder.appendRead(m_read[curI]);
					m_builder.appendReference('-');
				}
				else if (m_tableB[curI][curJ-1] + Constants.GAP == m_tableB[curI][curJ])
				{
					curJ--;
					m_builder.appendRead('-');
					m_builder.appendReference(m_region2[curJ]);
				}
				else if (m_tableB[curI-1][curJ-1] + Constants.matchScore(m_read[curI-1], m_region2[curJ-1]) == m_tableB[curI][curJ])
				{
					curI--; curJ--;
					m_builder.appendRead(m_read[curI]);
					m_builder.appendReference(m_region2[curJ]);
				}
				else if (m_tableMins[curI-1] + Constants.matchScore(m_read[curI-1], m_region2[curJ-1]) == m_tableB[curI][curJ])
				{
					tableB = false;
					m_builder.appendRead(m_read[curI-1]);
					m_builder.appendReference(m_region2[curJ-1]);

					m_builder.setBP2(m_gasvRegion.getRegionY().v - (curJ - 1), m_gasvRegion.getRightChromosome());

					curJ = m_minLocations[curI-1];
					curI--;

					m_builder.setBP1(m_gasvRegion.getRegionX().u + (curJ - 1), m_gasvRegion.getLeftChromosome());

					// notate the break in the output
					m_builder.appendRead('|');
					m_builder.appendReference('|');
				}
			}
			else
			{
				if (m_tableA[curI-1][curJ] + Constants.GAP == m_tableA[curI][curJ])
				{
					curI--;
					m_builder.appendRead(m_read[curI]);
					m_builder.appendReference('-');
				}
				else if (m_tableA[curI][curJ-1] + Constants.GAP == m_tableA[curI][curJ])
				{
					curJ--;
					m_builder.appendRead('-');
					m_builder.appendReference(m_region1[curJ]);
				}
				else if (m_tableA[curI-1][curJ-1] + Constants.matchScore(m_read[curI-1], m_region1[curJ-1]) == m_tableA[curI][curJ])
				{
					curI--; curJ--;
					m_builder.appendRead(m_read[curI]);
					m_builder.appendReference(m_region1[curJ]);
				}
			}
		}

		// put mismatches on the beginning of alignment strings
		for (int counter = curJ - 1; counter >= 0; counter--)
		{
			m_builder.appendRead('-');
			m_builder.appendReference(m_region2[counter]);
		}

		return m_builder.build();
	}

}