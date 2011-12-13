package splitread;

import java.io.PrintStream;

/**
 * Global constants passed as input parameters and fixed constants.
 * 
 * @author dstorch@cs.brown.edu
 * @since December 2011
 */
public class Constants
{	
	// output
	public enum OutputFormat
	{
		TABULAR,
		VERBOSE,
		CONCISE
	}
	public static OutputFormat OUTPUT_FORMAT = OutputFormat.VERBOSE;
	public static PrintStream OUTPUT_STREAM = new PrintStream(System.out);
	
	// defaults overridden by values passed from srGASV wrapper
	public static int FRAG_LENGTH_MIN = 100;
	public static int FRAG_LENGTH_MAX = 1000;
	public static int MIN_MAPQ = 35;
	public static int DELTA_WINDOW = 100;
	public static int MAX_ALIGNMENT_DIST = 30;
	public static int MIN_PER_SIDE = 4;
	
	public static String CHR_PREFIX = "";
	public static String SAMTOOLS = "lib/samtools";
	
	public static void setOutputFormatFromString(String format)
	{
		if (format.equals("tabular"))
		{
			Constants.OUTPUT_FORMAT = OutputFormat.TABULAR;
		}
		else if (format.equals("verbose"))
		{
			Constants.OUTPUT_FORMAT = OutputFormat.VERBOSE;
		}
		else if (format.equals("concise"))
		{
			Constants.OUTPUT_FORMAT = OutputFormat.CONCISE;
		}
		else
		{
			throw new IllegalArgumentException("unrecognized output format specification");
		}
	}
	
	// GASV outfile column numbers
	public static final int COL_CLUST_NAME = 0;
	public static final int COL_SV_TYPE = 3;
	public static final int COL_LPOS = 5;
	public static final int COL_RPOS = 6;
	public static final int COL_COORDS = 7;
	
    public static final int GAP = 1;
    private static final int[][] m_matchScores = 
      {{0, 1, 1, 1, 1},
       {1, 0, 1, 1, 1},
       {1, 1, 0, 1, 1},
       {1, 1, 1, 0, 1},
       {1, 1, 1, 1, 1}};
       
    private static int convertToCode(char letter)
    {
      if (letter == 'a' || letter == 'A') return 0;
      else if (letter == 'c' || letter == 'C') return 1;
      else if (letter == 'g' || letter == 'G') return 2;
      else if (letter == 't' || letter == 'T') return 3;
      else if (letter == 'n' || letter == 'N') return 4;
      else throw new IllegalArgumentException("non-DNA letter: " + letter);
    }
    
    public static int matchScore(char c1, char c2)
    {
      return m_matchScores[convertToCode(c1)][convertToCode(c2)];
    }
    
}