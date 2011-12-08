package splitread;

/**
 * Simple struct class which represents a point
 * in genome by genome space.
 * 
 * @author dstorch@cs.brown.edu
 * @since December 2011
 */
public class Point
{
	public int u;
	public int v;
	
	public Point(int u, int v)
	{
		this.u = u;
		this.v = v;
	}
	
	@Override
	public String toString()
	{
		return "(" + u + "," + v + ")";
	}
}
