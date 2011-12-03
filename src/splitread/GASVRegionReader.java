package splitread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
	
	public List<GASVRegion> read() throws IOException
	{	
		List<GASVRegion> allRegions = new LinkedList<GASVRegion>();
		
		String line;
		while ((line = m_reader.readLine()) != null)
		{
			GASVRegion region = new GASVRegion(line.split("\t"));
			allRegions.add(region);
		}
		
		return allRegions;
	}

}
