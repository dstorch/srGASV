package splitread;

public class Constants
{
	// make these more reasonable, or determined by GASV out
	public static final int FRAG_LENGTH_MIN = 100;
	public static final int FRAG_LENGTH_MAX = 400;
	public static final int MIN_MAPQ = 30;
	
	// GASV outfile column numbers
	public static final int COL_LPOS = 5;
	public static final int COL_RPOS = 6;
	public static final int COL_COORDS = 7;
	
	// reading and writing BAM
	public static final String BAM_EXTENSION = "_srGasv";
	
	// reading fasta
	public static final int FASTA_BUFSIZE = 1000;
	public static final String FASTA_TMPFILE = "data/hg19/temp.fa";
	
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