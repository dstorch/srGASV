package splitread;

import java.io.IOException;
import java.io.InputStream;

public class FastaReader implements IReferenceGenome
{	
	@Override
	public char[] getFragment(int chromosome, Point point) throws IOException
	{
		String chrstring = "chr" + Integer.toString(chromosome);
		String chrfile = getFastaName(chromosome);
		String regstr = chrstring + ":" + point.u + "-" + point.v;
		
		String command = "samtools faidx " + chrfile + " " + regstr;
		Process child = Runtime.getRuntime().exec(command);
		
		InputStream instream = child.getInputStream();
		int c;
		String result = "";
		while ((c = instream.read()) != (int) '\n') {}
		while ((c = instream.read()) != -1)
		{
			char letter = Character.toLowerCase((char) c);
			if (Constants.isDNALetter(letter))
			{
				result += (char) c;	
			}
		}
		
		return result.toCharArray();
	}

	private String getFastaName(int chromosome)
	{
		return Constants.HG_PATH + "chr" + chromosome + ".fa";
	}
	
}
