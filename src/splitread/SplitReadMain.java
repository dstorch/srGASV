package splitread;

import java.io.File;
import java.io.IOException;

import splitread.io.IReferenceGenome;

/**
 * Process command line arguments, and call
 * the processGASVOut() method of SplitReadWorker
 * 
 * @author dstorch@cs.brown.edu
 * @since December 2011
 */
public class SplitReadMain
{
    public static void main(String[] args)
    {	
    	File gasvOutfile = new File(args[0]);
    	File bamfile = new File(args[1]);
    	File fastafile = new File(args[2]);
    	File unmappedfile = new File(args[6]);
    	
    	if (!gasvOutfile.exists() || !gasvOutfile.isFile())
    	{
    		throw new IllegalArgumentException("could not find GASV regions file: " + gasvOutfile.getName());
    	}
    	if (!bamfile.exists() || !bamfile.isFile())
    	{
    		throw new IllegalArgumentException("could not find BAM file: " + bamfile.getName());
    	}
    	if (!fastafile.exists() || !fastafile.isFile())
    	{
    		throw new IllegalArgumentException("could not find fasta file: " + fastafile.getName());
    	}
    	
    	// set the fasta file name
    	IReferenceGenome.GenomeFactory.getInstance().setFastaName(fastafile.getAbsolutePath());
    	
    	Constants.FRAG_LENGTH_MIN = Integer.parseInt(args[3]);
    	Constants.FRAG_LENGTH_MAX = Integer.parseInt(args[4]);
    	Constants.MIN_MAPQ = Integer.parseInt(args[5]);
    	Constants.DELTA_WINDOW = Integer.parseInt(args[8]);
    	Constants.setOutputFormatFromString(args[9]);
    	Constants.MAX_ALIGNMENT_DIST = Integer.parseInt(args[10]);
    	
    	if (!args[7].equals("default")) Constants.CHR_PREFIX = args[7];
    	
    	SplitReadWorker srw = null;
    	int exitCode = 0;
		try
		{
			// do all processing
			srw = new SplitReadWorker(bamfile, gasvOutfile, unmappedfile);
	    	srw.processGASVOut();
		}
		catch (SplitReadException e)
		{
			System.err.println(e.getMessage());
			exitCode = 1;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			exitCode = 2;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			exitCode = 3;
		}
		finally
		{
			srw.cleanup();
			System.exit(exitCode);
		}

    }
}