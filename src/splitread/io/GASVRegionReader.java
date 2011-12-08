package splitread.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import splitread.GASVRegion;
import splitread.SplitReadException;
import splitread.SplitReadWorker;

public class GASVRegionReader
{
	
	private BufferedReader m_reader;
	
	public GASVRegionReader(File infile)
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
	
	public void read(SplitReadWorker srw) throws IOException, SplitReadException
	{	
		String line;
		while ((line = m_reader.readLine()) != null)
		{
			GASVRegion region = new GASVRegion(line.split("\t"));
			srw.processOneRegion(region);
		}
	}
	
	public void close() throws IOException
	{
		m_reader.close();
	}

}
