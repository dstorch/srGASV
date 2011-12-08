package splitread;

/**
 * Represents the type of a structural variant event.
 * Each GASVRegion object has an associated sv type.
 * 
 * @author dstorch@cs.brown.edu
 * @since December 2011
 */
public enum SVType
{
	DELETION,
	INVERSION,
	INSERTION,
	TRANSLOCATION
}
