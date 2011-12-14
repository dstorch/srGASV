package splitread.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import splitread.GASVCluster;
import splitread.SplitReadException;
import splitread.SplitReadWorker;

/**
 * Reader for the input file which contains
 * GASV clusters. Each line is used to
 * construct a GASVCluster, and each resulting
 * cluster is passed back to the SplitReadWorker
 * to be processed.
 * 
 * @author dstorch
 * @since December 2011
 */
public class GASVClusterReader
{
	
	private BufferedReader m_reader;
	
	public GASVClusterReader(File infile)
	{
		try
		{
			m_reader = new BufferedReader(new FileReader(infile));
		}
		catch (FileNotFoundException e)
		{
			// should never happen
			throw new IllegalArgumentException("GASV outfile not found");
		}
	}
	
	public void read(SplitReadWorker srw) throws IOException, SplitReadException, ClassNotFoundException
	{	
		String line;
		while ((line = m_reader.readLine()) != null)
		{
			GASVCluster region = new GASVCluster(line.split("\t"));
			srw.processOneRegion(region);
		}
	}
	
	public void close() throws IOException
	{
		m_reader.close();
	}

}
