package splitread.io;

import java.io.IOException;
import java.io.InputStream;

import splitread.Constants;
import splitread.Point;
import splitread.Utils;

/**
 * Uses samtools faidx command to
 * extract sequence from the reference genome.
 * This is launched as a subprocess, and the result is read
 * through the input stream of the subprocess.
 * 
 * @author dstorch
 * @since December 2011
 */
public class FastaReader implements IReferenceGenome
{	
	// the name of the fasta file containing the sequence
	private String m_fastaName;
	
	@Override
	public char[] getFragment(int chromosome, Point point) throws IOException
	{
		String chrstring = "chr" + Integer.toString(chromosome);
		String regstr = chrstring + ":" + point.u + "-" + point.v;
		
		String command = Constants.SAMTOOLS + " faidx " + m_fastaName + " " + regstr;
		Process child = Runtime.getRuntime().exec(command);
		
		InputStream instream = child.getInputStream();
		int c;
		String result = "";
		while ((c = instream.read()) != (int) '\n') {}
		while ((c = instream.read()) != -1)
		{
			char letter = Character.toLowerCase((char) c);
			if (Utils.isDNALetter(letter))
			{
				result += (char) c;	
			}
		}
		
		child.destroy();
		
		return result.toCharArray();
	}
	
	public void setFastaName(String fastaName)
	{
		m_fastaName = fastaName;
	}
	
}
