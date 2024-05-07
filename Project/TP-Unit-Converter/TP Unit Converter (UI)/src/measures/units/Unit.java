package measures.units;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * A base abstract class for all units.
 * A unit is composed of:
 * <ul>
 * <li>A description (which could appear in som UI)</li>
 * <li>A symbol (used to place near values expressed with this unit)</li>
 * <li>An optional value stored in this unit</li>
 * </ul>
 * A Unit shall:
 * <ul>
 * <li>Be printable (through toString) with description and/or symbol</li>
 * <li>Be comparable with equals(Object) as well as compareTo(Unit<?>)</li>
 * <li>Be hashable (through hashCode)</li>
 * <li>format a provided value into a {@link String} that can be presented in a
 * UI. e.g. "96.5 [km/h]"</li>
 * <li>format the internal optional value (if available) in the same way as the
 * previous statement</li>
 * <li>Convert value from another unit into its internal value</li>
 * </ul>
 * All Units are:
 * <ul>
 * 	<li>Comparable in order to be sorted</li>
 * </ul>
 * And therefore a Unit also contains an internal enum defining the order to
 * apply when comparing Units.
 * @author davidroussel
 * @param <E> the type of data measured with this unit
 */
public abstract class Unit<E extends Comparable<E>> implements Comparable<Unit<?>>
{
	/**
	 * Description of this unit.
	 * E.g. : "Kilometers per Hour"
	 * @implSpec Shall never be null
	 */
	protected final String description;

	/**
	 * Symbol to express this unit.
	 * E.g. : "Km/h"
	 * @implSpec Shall never be null
	 */
	protected final String symbol;

	/**
	 * A value currently stored in this unit.
	 * In order to be converted to some other units or simply accessed for
	 * display purposes
	 */
	protected Optional<E> value;

	/**
	 * Current Sorting order to apply when comparing units
	 * @see #compareTo(Unit)
	 */
	protected static SortOrder order = SortOrder.NAME_ASCENDING;

	/**
	 * Valued constructor
	 * @param description the description of this unit
	 * @param symbol  the symbol used with this unit
	 * @throws NullPointerException  if any of the provided values are null
	 * @implNote {@link #value} should be initialized as {@link Optional#empty()}
	 */
	public Unit(String description,
	            String symbol) throws NullPointerException
	{
		Objects.requireNonNull(description);
		Objects.requireNonNull(symbol);
		this.description = description;
		this.symbol = symbol;
		value = Optional.empty();
	}

	/**
	 * Description accessor
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Symbol accessor
	 * @return the symbol
	 */
	public String getSymbol()
	{
		return symbol;
	}

	/**
	 * Indicator of an internal present {@link #value}
	 * @return true if {@link #value} contains an actual value
	 */
	public boolean hasValue()
	{
		return value.isPresent();
	}

	/**
	 * Accessor to optional value
	 * @return the stored value (iff available)
	 * @throws NoSuchElementException if there is no such value
	 */
	public E getValue() throws NoSuchElementException
	{
		return value.get();
	}

	/**
	 * Mutator of optional value
	 * @param value the value to store in oprional value
	 */
	public void setValue(E value)
	{
		this.value = Optional.of(value);
	}

	/**
	 * Clears internal {@link #value}.
	 * Sets {@link #value} as an empty {@link Optional}
	 */
	public void clearValue()
	{
		value = Optional.empty();
	}

	/**
	 * Get the factor of this unit (for sorting purposes).
	 * Base (SI) units will have a 1.0 factor.
	 * Derived units will have other factors
	 * @return the factor of this unit
	 */
	public abstract double getFactor();

	/**
	 * Get the relative factor of this unit compared to the provided unit
	 * @param unit the other unit to compute relative factor
	 * @return the relative factor between provided unit and this unit
	 */
	public double getRelativeFactor(Unit<?> unit)
	{
		return unit.getFactor() / getFactor();
	}

	/**
	 * Format the internal value for printing.
	 * e.g. "96.5" in "96.5 Km/h"
	 * @return a formatted String of the internal value
	 */
	public abstract String formatValue();

	/**
	 * Compare this unit to another unit.
	 * Partial implementation using names {@link Unit#description} and
	 * {@link Unit#symbol} only.
	 * @param unit the other unit to compare to
	 * @return -1 if this unit is considered smaller than the provided unit. 0
	 * if both units are considered equals. And 1 otherwise
	 * @throws NullPointerException if the specified object is null
	 */
	@Override
	public int compareTo(Unit<?> unit)
	    throws NullPointerException
	{
		Objects.requireNonNull(unit);

		int descriptionCompare = description.compareTo(unit.description);
		int symbolCompare = symbol.compareTo(unit.symbol);
		double factor = getFactor();
		double otherFactor = unit.getFactor();
		int factorCompare = factor < otherFactor ? -1 : factor == otherFactor ? 0 : 1;
		int result = 0;
		switch (order)
		{
			case NAME_ASCENDING:
				result = (descriptionCompare == 0 ? symbolCompare : descriptionCompare);
				break;
			case NAME_DESCENDING:
				result = (descriptionCompare == 0 ? -symbolCompare : -descriptionCompare);
				break;
			/*
			 * We don't know yet how to compare with factors since
			 * they don't exist yet
			 */
			case FACTOR_ASCENDING:
				result = factorCompare;
				break;
			case FACTOR_DESCENDING:
				result = -factorCompare;
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + order);
		}
		return result;
	}

	/**
	 * Partial hash code based solely on {@link #description} and
	 * {@link #symbol}
	 * @return a partial hash code to be used by subclasses
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + description.hashCode();
		result = (prime * result) + symbol.hashCode();
		return result;
	}

	/**
	 * Compare this unit to another object
	 * @param obj the other object to compare to
	 * @return true if the other object is a non null {@link Unit} which
	 * compares to the current {@link Unit} with 0 value and contains the same
	 * description and symbol
	 * @see #compareTo(Unit)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj == null)
		{
			return false;
		}

		if (!(obj instanceof Unit<?>))
		{
			return false;
		}

		Unit<?> other = (Unit<?>) obj;

		if (compareTo(other) != 0)
		{
			return false;
		}

		if (!other.description.equals(description))
		{
			return false;
		}

		if (!(other.symbol.equals(symbol)))
		{
			return false;
		}

		return true;
	}

	/**
	 * String representation of this unit based on {@link #description} and
	 * {@link #symbol}
	 * @return a new String representation of this unit based on
	 * {@link #description} and {@link #symbol}
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(description);
		builder.append(" (");
		builder.append(symbol);
		builder.append(')');
		return builder.toString();
	}

	/**
	 * Get the current order used to compare Units
	 * @return the current order
	 */
	public static SortOrder getOrder()
	{
		return order;
	}

	/**
	 * Sets a new order to compare units
	 * @param order the order to set
	 */
	public static void setOrder(SortOrder order)
	{
		Unit.order = order;
	}
}
