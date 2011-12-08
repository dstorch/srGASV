package splitread;

public class Utils
{
    public static boolean isDNALetter(char letter)
    {
    	return (letter == 'a' || letter == 'A' ||
    			letter == 'g' || letter == 'G' ||
    			letter == 'c' || letter == 'C' ||
    			letter == 't' || letter == 'T' ||
    			letter == 'n' || letter == 'N');
    }
    
    public static char complementBase(char letter)
    {
        if (letter == 'a' || letter == 'A') return 't';
        else if (letter == 'c' || letter == 'C') return 'g';
        else if (letter == 'g' || letter == 'G') return 'c';
        else if (letter == 't' || letter == 'T') return 'a';
        else if (letter == 'n' || letter == 'N') return 'N';
        else throw new IllegalArgumentException("non-DNA letter: " + letter);
    }
    
    public static char[] complement(char[] letters)
    {
    	char[] outletters = new char[letters.length];
    	
    	for (int i = 0; i < letters.length; i++)
    	{
    		outletters[i] = complementBase(letters[i]);
    	}
    	
    	return outletters;
    }
    
    public static char[] reverse(char[] sequence)
    {
    	char[] outArray = new char[sequence.length];
    	
    	// reverse
    	for (int i = 0; i < sequence.length; i++)
    	{
    		int other = sequence.length - 1 - i;
    		outArray[i] = sequence[other];
    	}
    	
    	return outArray;
    }
    
    public static char[] reverseComplement(char[] sequence)
    {
    	char[] outArray = new char[sequence.length];
    	
    	// reverse
    	for (int i = 0; i < sequence.length; i++)
    	{
    		int other = sequence.length - 1 - i;
    		outArray[i] = complementBase(sequence[other]);
    	}
    	
    	return outArray;
    }
    
    public static SVType svTypeFromString(String typeStr) throws SplitReadException
    {
    	if (typeStr.equals("D"))
    	{
    		return SVType.DELETION;
    	}
    	else if (typeStr.equals("I"))
    	{
    		return SVType.INVERSION;
    	}
    	else
    	{
    		throw new SplitReadException("unknown sv type");
    	}
    }
}
