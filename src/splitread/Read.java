package splitread;

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
