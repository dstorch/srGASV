package splitread.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import splitread.Point;

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
	
	char[] getFragment(int chromosome, Point point) throws FileNotFoundException, IOException;
	
	public void setFastaName(String fastaName);
}