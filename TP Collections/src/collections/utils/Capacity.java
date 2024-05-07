package collections.utils;

import java.util.Arrays;

/**
 * Interface for all classes using an array as internal storage requiring:
 * <ul>
 * <li>a Capacity to define the internal array's length</li>
 * <li>a Capacity Increment to define the amount by which the internal array
 * must grow or shrink when a reallocation of internal array is required</li>
 * <li>
 * </ul>
 * @author davidroussel
 * @param <E> the type of elements stored in interna array
 */
public interface Capacity<E>
{
	/**
	 * The default capacity of internal array;
	 */
	public static final int DEFAULT_CAPACITY = 10;

	/**
	 * The default capacity increment of internal array
	 */
	public static final int DEFAULT_CAPACITY_INCREMENT = 10;

	/**
	 * Capacity accessor
	 * @return the current capacity of internal array
	 */
	public int getCapacity();

	/**
	 * Capacity increment caccessor
	 * @return the current capacity increment of internal array
	 */
	public int getCapacityIncrement();

	/**
	 * Grow the internal array by provided amount
	 * @param amount the amount to grow internal storage
	 * @throws IllegalArgumentException if provided amount is negative or null
	 */
	public abstract void grow(int amount) throws IllegalArgumentException;

	/**
	 * Increases the capacity of internal array, if necessary, to ensure that it
	 * can hold at least the number of elements specified by the minimum
	 * capacity argument.
	 * @param minCapacity the desired minimum capacity
	 */
    public default void ensureCapacity(int minCapacity)
    {
    	int capacity = getCapacity();
		if (minCapacity > capacity)
		{
			grow(minCapacity - capacity);
		}
    }

    /**
     * Resize provided array to required size
     * @param <E> the type of elements stored in array
     * @param array the array to resize
     * @param requiredSize the required size (can evt be smaller than array's length)
     * @return a resized copy of provided array
     * @see java.util.Arrays#copyOf(Object[], int)
     */
    public static <E> E[] resizeArray(E[] array, int requiredSize)
    {
    	return Arrays.copyOf(array, requiredSize);
    }
}
