package splitread;

/**
 * Representation of a Read read in from SQLite
 * database. Associates a read name with its sequence.
 * 
 * @author dstorch@cs.brown.edu
 * @since December 2011
 */
public class Read
{
	private String m_name;
	private String m_sequence;
	
	public Read(String name, String sequence)
	{
		m_name = name;
		m_sequence = sequence;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public String getSequence()
	{
		return m_sequence;
	}

}
