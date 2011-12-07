package splitread;

public class Constants
{	
	// make these more reasonable, or determined by GASV out
	public static int FRAG_LENGTH_MIN = 100;
	public static int FRAG_LENGTH_MAX = 1000;
	public static int MIN_MAPQ = 35;
	
	// GASV outfile column numbers
	public static final int COL_CLUST_NAME = 0;
	public static final int COL_LPOS = 5;
	public static final int COL_RPOS = 6;
	public static final int COL_COORDS = 7;
	
    public static final int GAP = 2;
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
    
    public static boolean isDNALetter(char letter)
    {
    	return (letter == 'a' || letter == 'A' ||
    			letter == 'g' || letter == 'G' ||
    			letter == 'c' || letter == 'C' ||
    			letter == 't' || letter == 'T' ||
    			letter == 'n' || letter == 'N');
    }
    
    public static int matchScore(char c1, char c2)
    {
      return m_matchScores[convertToCode(c1)][convertToCode(c2)];
    }
}