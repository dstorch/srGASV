package splitread;

import java.io.File;
import java.io.IOException;

public class SplitReadMain
{
    public static void main(String[] args) throws IOException, InterruptedException
    {	
    	File gasvOutfile = new File(args[0]);
    	File bamfile = new File(args[1]);
    	
    	if (!gasvOutfile.exists() || !gasvOutfile.isFile())
    	{
    		throw new IllegalArgumentException("could not find GASV regions file: " + gasvOutfile.getName());
    	}
    	if (!bamfile.exists() || !bamfile.isFile())
    	{
    		throw new IllegalArgumentException("could not find BAM file: " + bamfile.getName());
    	}
    	
    	Constants.FRAG_LENGTH_MIN = Integer.parseInt(args[2]);
    	Constants.FRAG_LENGTH_MAX = Integer.parseInt(args[3]);
    	Constants.MIN_MAPQ = Integer.parseInt(args[4]);
    	Constants.HG_PATH = args[5];
    	
    	SplitReadWorker srw = new SplitReadWorker(bamfile, gasvOutfile);
    	srw.processGASVOut();
    }
}