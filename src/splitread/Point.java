package splitread;

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
