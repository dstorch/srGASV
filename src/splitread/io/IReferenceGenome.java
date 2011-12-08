package splitread.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import splitread.Point;

/**
 * Interface for obtaining sequence from the
 * reference genome. Uses a singleton FastaReader
 * as the underlying implementation.
 * 
 * @author dstorch
 * @since December 2011
 */
public interface IReferenceGenome
{
	public static class GenomeFactory
	{
		public static FastaReader INSTANCE = new FastaReader();
		
		public static IReferenceGenome getInstance()
		{
			return INSTANCE;
		}
	}
	
	// get sequence corresponding to the region stored in point as an array of chars
	char[] getFragment(int chromosome, Point point) throws FileNotFoundException, IOException;
	
	public void setFastaName(String fastaName);
}
