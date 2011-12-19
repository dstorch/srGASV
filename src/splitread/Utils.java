package splitread;

import java.util.List;

/**
 * Static utility methods.
 * 
 * @author dstorch@cs.brown.edu
 * @since December 2011
 */
public class Utils
{
	public static boolean isDNALetter(char letter)
	{
		return (letter == 'a' || letter == 'A' ||
				letter == 'g' || letter == 'G' ||
				letter == 'c' || letter == 'C' ||
				letter == 't' || letter == 'T' ||
				letter == 'n' || letter == 'N');
	}

	public static char complementBase(char letter)
	{
		if (letter == 'a' || letter == 'A') return 't';
		else if (letter == 'c' || letter == 'C') return 'g';
		else if (letter == 'g' || letter == 'G') return 'c';
		else if (letter == 't' || letter == 'T') return 'a';
		else return letter;
	}

	public static char[] complement(char[] letters)
	{
		char[] outletters = new char[letters.length];

		for (int i = 0; i < letters.length; i++)
		{
			outletters[i] = complementBase(letters[i]);
		}

		return outletters;
	}

	public static char[] reverse(char[] sequence)
	{
		char[] outArray = new char[sequence.length];

		// reverse
		for (int i = 0; i < sequence.length; i++)
		{
			int other = sequence.length - 1 - i;
			outArray[i] = sequence[other];
		}

		return outArray;
	}

	public static char[] reverseComplement(char[] sequence)
	{
		char[] outArray = new char[sequence.length];

		// reverse
		for (int i = 0; i < sequence.length; i++)
		{
			int other = sequence.length - 1 - i;
			outArray[i] = complementBase(sequence[other]);
		}

		return outArray;
	}

	public static SVType svTypeFromString(String typeStr) throws SplitReadException
	{
		if (typeStr.equals("D"))
		{
			return SVType.DELETION;
		}
		else if (typeStr.equals("I") || typeStr.equals("I+") || typeStr.equals("I-") || typeStr.equals("IR"))
		{
			return SVType.INVERSION;
		}
		else
		{
			throw new SplitReadException("unknown sv type");
		}
	}

	/**
	 * Determine whether a predicted breakpoint is inside the breakpoint polygon
	 * 
	 * @param cluster - a GASV cluster storing polygon coordinates
	 * @param bp1 - predicted breakpoint 1
	 * @param bp2 - predicted breakpoint 2
	 * @return true if the target is in the cluster's breakpoint; false otherwise
	 */
	public static boolean pointInPoly(GASVCluster cluster, int bp1, int bp2)
	{
		List<Point> coords = cluster.getCoords();
		
		int numCoords = coords.size();
		int[] xpoints = new int[numCoords];
		int[] ypoints = new int[numCoords];
		
		int i = 0;
		for (Point p : coords)
		{
			xpoints[i] = p.u;
			ypoints[i] = p.v;
			i++;
		}
		
		return pointInPoly(xpoints, ypoints, numCoords, bp1, bp2);
	}
	
	/**
	 * Determine whether a predicted breakpoint is inside the breakpoint polygon
	 * 
	 * @param xpoints - x-coordinates of polygon vertices
	 * @param ypoints - y-coordinates of polygon vertices
	 * @param npoints - number of points
	 * @param xt - x-coordinate of target which we are checking
	 * @param yt - y-coordinate of target which we are checking
	 * 
	 * @return true if the target is in the polygon; false otherwise
	 */
	public static boolean pointInPoly(int[] xpoints, int[] ypoints, int npoints, int xt, int yt)
	{
		int xnew,ynew;
		int xold,yold;
		int x1,y1;
		int x2,y2;
		int i;
		boolean inside = false;

		if (npoints < 3) {
			return false;
		}
		xold=xpoints[npoints-1];
		yold=ypoints[npoints-1];
		for (i=0 ; i < npoints ; i++) {
			xnew=xpoints[i]; //poly[i][0];
			ynew=ypoints[i]; //poly[i][1];
			if (xnew > xold) {
				x1=xold;
				x2=xnew;
				y1=yold;
				y2=ynew;
			}
			else {
				x1=xnew;
				x2=xold;
				y1=ynew;
				y2=yold;
			}
			if ((xnew < xt) == (xt <= xold)         /* edge "open" at left end */
					&& ((long)yt-(long)y1)*(long)(x2-x1)
					< ((long)y2-(long)y1)*(long)(xt-x1)) {
				inside = !inside;
			}
			xold=xnew;
			yold=ynew;
		}

		return inside;
	}
}
