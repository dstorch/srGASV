package splitread.align;

import splitread.Constants;
import splitread.GASVCluster;
import splitread.Read;

/**
 * Implements split read alignment for
 * deletions.
 * 
 * @author dstorch@cs.brown.edu
 * @since December 2011
 */
public class DeletionAligner extends Aligner
{

	public DeletionAligner(Read read, GASVCluster region)
	{
		super(read, region);
	}

	@Override
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
		for (int i = 1; i <= m_seq.length; i++)
		{
			m_tableA[i][0] = i * Constants.GAP;
		}

		// fill table a
		for (int i = 1; i <= m_seq.length; i++)
		{
			m_tableMins[i] = Integer.MAX_VALUE;

			for (int j = 1; j <= m_region1.length; j++)
			{
				m_tableA[i][j] = Math.min(m_tableA[i-1][j] + Constants.GAP,
						Math.min(m_tableA[i][j-1] + Constants.GAP,
								m_tableA[i-1][j-1] + Constants.matchScore(m_seq[i-1], m_region1[j-1])));

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
		for (int i = 1; i <= m_seq.length; i++)
		{
			m_tableB[i][0] = i * Constants.GAP;
		}

		// fill table b
		for (int i = 1; i <= m_seq.length; i++)
		{
			for (int j = 1; j <= m_region2.length; j++)
			{
				m_tableB[i][j] = Math.min(m_tableB[i-1][j] + Constants.GAP,
						Math.min(m_tableB[i][j-1] + Constants.GAP,
								Math.min(m_tableB[i-1][j-1] + Constants.matchScore(m_seq[i-1], m_region2[j-1]),
										m_tableMins[i-1] + Constants.matchScore(m_seq[i-1], m_region2[j-1]))));
			}
		}
	}

	private Alignment traceback()
	{   
		// find the min in the last row of table b
		int minVal = Integer.MAX_VALUE;
		int minPos = -1;
		int curI = m_seq.length;
		for (int j = 0; j <= m_region2.length; j++) {
			if (m_tableB[curI][j] < minVal)
			{
				minVal = m_tableB[curI][j];
				minPos = j;
			}
		}

		m_builder.setScore(minVal);

		// put mismatches on the end of alignment strings
		for (int counter = m_region2.length - 1; counter >= minPos; counter--)
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
					m_builder.appendRead(m_seq[curI]);
					m_builder.appendReference('-');
				}
				else if (m_tableB[curI][curJ-1] + Constants.GAP == m_tableB[curI][curJ])
				{
					curJ--;
					m_builder.appendRead('-');
					m_builder.appendReference(m_region2[curJ]);
				}
				else if (m_tableB[curI-1][curJ-1] + Constants.matchScore(m_seq[curI-1], m_region2[curJ-1]) == m_tableB[curI][curJ])
				{
					curI--; curJ--;
					m_builder.appendRead(m_seq[curI]);
					m_builder.appendReference(m_region2[curJ]);
				}
				else if (m_tableMins[curI-1] + Constants.matchScore(m_seq[curI-1], m_region2[curJ-1]) == m_tableB[curI][curJ])
				{
					tableB = false;
					m_builder.appendRead(m_seq[curI-1]);
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
					m_builder.appendRead(m_seq[curI]);
					m_builder.appendReference('-');
				}
				else if (m_tableA[curI][curJ-1] + Constants.GAP == m_tableA[curI][curJ])
				{
					curJ--;
					m_builder.appendRead('-');
					m_builder.appendReference(m_region1[curJ]);
				}
				else if (m_tableA[curI-1][curJ-1] + Constants.matchScore(m_seq[curI-1], m_region1[curJ-1]) == m_tableA[curI][curJ])
				{
					curI--; curJ--;
					m_builder.appendRead(m_seq[curI]);
					m_builder.appendReference(m_region1[curJ]);
				}
			}
		}

		// put mismatches on the beginning of alignment strings
		for (int counter = curJ - 1; counter >= 0; counter--)
		{
			m_builder.appendRead('-');
			m_builder.appendReference(m_region1[counter]);
		}

		return m_builder.build();
	}
}
