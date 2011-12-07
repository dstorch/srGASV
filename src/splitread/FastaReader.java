package splitread;

import java.io.IOException;
import java.io.InputStream;

public class FastaReader implements IReferenceGenome
{	
	
	private String m_fastaName;
	
	@Override
	public char[] getFragment(int chromosome, Point point) throws IOException
	{
		String chrstring = "chr" + Integer.toString(chromosome);
		String regstr = chrstring + ":" + point.u + "-" + point.v;
		
		String command = "samtools faidx " + m_fastaName + " " + regstr;
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
		
		// TODO is this necessary?
		child.destroy();
		
		return result.toCharArray();
	}
	
	public void setFastaName(String fastaName)
	{
		m_fastaName = fastaName;
	}
	
}
