/**
 *
 */
package measures.units;

/**
 * A Dummy implementation for {@link Unit}s.
 * Just to provide content for UI
 * @implNote TO BE REPLACED BY REAL UNITS HIERARCHY LATER
 * @author davidroussel
 */
public class DummyUnit extends Unit<Double>
{

	/**
	 * Valued constructor
	 * @param description the description of this unit
	 * @param symbol  the symbol used with this unit
	 * @throws NullPointerException  if any of the provided values are null
	 */
	public DummyUnit(String description, String symbol)
	    throws NullPointerException
	{
		super(description, symbol);
	}

	/**
	 * Get the factor of this unit (for sorting purposes).
	 * This dummy implementation always return 1.0
	 * @return the factor of this unit
	 */
	@Override
	public double getFactor()
	{
		return 1.0;
	}

	/**
	 * Format the internal value for printing.
	 * @return a formatted String of the internal value
	 * @implNote In this cas no formatting is applied
	 */
	@Override
	public String formatValue()
	{
		if (!value.isEmpty())
		{
			return value.get().toString();
		}

		return "";
	}

}
