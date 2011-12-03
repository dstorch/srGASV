package splitread;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GASVRegion
{
	private int m_leftChromosome;
	private int m_rightChromosome;
	private List<Point> m_coords;
	private Point m_regionX;
	private Point m_regionY;

	private char[] m_fragX;
	private char[] m_fragY;
	
	private Set<String> m_readNames;

	public GASVRegion(String[] cols) throws IOException
	{
		m_leftChromosome = Integer.parseInt(cols[Constants.COL_LPOS].trim());
		m_rightChromosome = Integer.parseInt(cols[Constants.COL_RPOS].trim());
		m_coords = new LinkedList<Point>();

		m_regionX = new Point(Integer.MAX_VALUE, Integer.MIN_VALUE);
		m_regionY = new Point(Integer.MAX_VALUE, Integer.MIN_VALUE);

		String[] coords = cols[Constants.COL_COORDS].split(",");

		// always expect even number of coords
		assert(coords.length % 2 == 0);

		for (int i = 0; i < coords.length; i += 2)
		{
			int c1 = Integer.parseInt(coords[i].trim());
			int c2 = Integer.parseInt(coords[i+1].trim());
			m_coords.add(new Point(c1, c2));

			if (c1 < m_regionX.u) m_regionX.u = c1;
			if (c1 > m_regionX.v) m_regionX.v = c1;
			if (c2 < m_regionY.u) m_regionY.u = c2;
			if (c2 > m_regionY.v) m_regionY.v = c2;
		}

		IReferenceGenome genome = IReferenceGenome.GenomeFactory.getInstance();
		m_fragX = genome.getFragment(m_leftChromosome, m_regionX);
		m_fragY = genome.getFragment(m_rightChromosome, m_regionY);
	}
	
	public void setCandidateReads(Set<String> readNames)
	{
		m_readNames = readNames;
	}
	
	public Set<String> getCandidateReads()
	{
		return m_readNames;
	}

	public int getLeftChromosome()
	{
		return m_leftChromosome;
	}

	public int getRightChromosome()
	{
		return m_rightChromosome;
	}

	public List<Point> getCoords()
	{
		return m_coords;
	}

	public Point getRegionX()
	{
		return m_regionX;
	}

	public Point getRegionY()
	{
		return m_regionY;
	}
	
	public char[] getFragX()
	{
		return m_fragX;
	}
	
	public char[] getFragY()
	{
		return m_fragY;
	}

}
