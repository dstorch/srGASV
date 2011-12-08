package splitread;

/**
 * Use for throwing split-read specific exceptions.
 * 
 * @author dstorch@cs.brown.edu
 * @since December 2011
 */
public class SplitReadException extends Exception
{
	private static final long serialVersionUID = 1L;

	public SplitReadException(String message)
	{
		super(message);
	}
}
