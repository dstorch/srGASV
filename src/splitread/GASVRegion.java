package splitread;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import splitread.io.IReferenceGenome;

public class GASVRegion
{
	private int m_leftChromosome;
	private int m_rightChromosome;
	private List<Point> m_coords;
	private Point m_regionX;
	private Point m_regionY;

	private char[] m_fragX;
	private char[] m_fragY;
	
	private String m_clusterName;
	private Set<String> m_readNames;
	
	private SVType m_svType;
	
	public GASVRegion(String[] cols) throws IOException, SplitReadException
	{
		m_leftChromosome = Integer.parseInt(cols[Constants.COL_LPOS].trim());
		m_rightChromosome = Integer.parseInt(cols[Constants.COL_RPOS].trim());
		m_clusterName = cols[Constants.COL_CLUST_NAME].trim();
		
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
		
		// boost window size by a constant
		m_regionX.u -= Constants.DELTA_WINDOW;
		m_regionX.v += Constants.DELTA_WINDOW;
		m_regionY.u -= Constants.DELTA_WINDOW;
		m_regionY.v += Constants.DELTA_WINDOW;

		IReferenceGenome genome = IReferenceGenome.GenomeFactory.getInstance();
		m_fragX = genome.getFragment(m_leftChromosome, m_regionX);
		m_fragY = genome.getFragment(m_rightChromosome, m_regionY);
		
		m_svType = Utils.svTypeFromString(cols[Constants.COL_SV_TYPE]);
	}
	
	public GASVRegion(String[] dummyCols, boolean dummy)
	{
		m_leftChromosome = Integer.parseInt(dummyCols[Constants.COL_LPOS].trim());
		m_rightChromosome = Integer.parseInt(dummyCols[Constants.COL_RPOS].trim());
		m_clusterName = dummyCols[Constants.COL_CLUST_NAME].trim();
		
		m_coords = new LinkedList<Point>();

		m_regionX = new Point(Integer.MAX_VALUE, Integer.MIN_VALUE);
		m_regionY = new Point(Integer.MAX_VALUE, Integer.MIN_VALUE);

		String[] coords = dummyCols[Constants.COL_COORDS].split(",");

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

	}
	
	@Override
	public String toString()
	{
		return m_leftChromosome + ":" + getRegionX().u + "-" + getRegionX().v + ", " +
		       m_rightChromosome + ":" + getRegionY().u + "-" + getRegionY().v;
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
	
	public String getClusterName()
	{
		return m_clusterName;
	}
	
	public static GASVRegion getDummy() throws IOException
	{
		String[] cols = new String[8];
		cols[0] = "c622_-22.4248_-696.71";
		cols[5] = "1";
		cols[6] = "1";
		cols[7] = "34874107, 34884613, 34874008, 34884613, 34873958, 34884563, 34873958, 34884464";
		
		return new GASVRegion(cols, true);
	}
	
	public SVType getSVType()
	{
		return m_svType;
	}

}
