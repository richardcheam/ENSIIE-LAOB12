package collections;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * A dummy {@link Collection} containing a real collection just to show what are
 * the methods that need to be overridden when inheriting from
 * {@link AbstractCollection}.
 * @author davidroussel
 * @param <E> The Type of objects in this collection
 */
public class MyDummyCollection<E> extends AbstractCollection<E>
{
	/**
	 * The internal collection used by this collection (which is why it's a dummy one)
	 */
	private Collection<E> internalCollection;

	/**
	 * Default constructor.
	 * Creates an empty collection
	 */
	public MyDummyCollection()
	{
		internalCollection = new ArrayList<>();
	}

	/**
	 * Copy constructor from collection
	 * @param col the collection to copy
	 */
	public MyDummyCollection(Collection<E> col)
	{
		this();
		for (E elt : col)
		{
			add(elt);
		}
	}

	/**
	 * Ensures that this collection contains the specified element.
	 * Returns {@code true} if this collection changed as a result of the call.
	 * <p>
	 * Collections that support this operation may place limitations on what
	 * elements may be added to this collection. In particular, this
	 * collections will refuse to add {@code null} elements, and others will
	 * impose restrictions on the type of elements that may be added.
	 * Collection classes should clearly specify in their documentation any
	 * restrictions on what elements may be added.
	 * <p>
	 * If a collection refuses to add a particular element for any reason
	 * other than that it already contains the element, it <i>must</i> throw
	 * an exception (rather than returning {@code false}). This preserves
	 * the invariant that a collection always contains the specified element
	 * after this call returns.
	 * @param e element whose presence in this collection is to be ensured
	 * @return {@code true} if this collection changed as a result of the
	 * call
	 * @throws NullPointerException if the specified element is null and this
	 * collection does not permit null elements
	 */
	@Override
	public boolean add(E e) throws NullPointerException
	{
		Objects.requireNonNull(e);

		return internalCollection.add(e);
	}

	/**
	 * Returns an iterator over the elements contained in this collection.
	 * @return an iterator over the elements contained in this collection
	 */
	@Override
	public Iterator<E> iterator()
	{
		return internalCollection.iterator();
	}

    /**
     * Returns the number of elements in this collection.
     * @return the number of elements in this collection
     */
	@Override
	public int size()
	{
		return internalCollection.size();
	}

	/**
	 * Returns a hash code value for the object. This method is
	 * supported for the benefit of hash tables such as those provided by
	 * {@link java.util.HashMap}.
	 * @return a hash code value for this object.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public int hashCode()
	{
		return internalCollection.hashCode();
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * <p>
	 * The {@code equals} method implements an equivalence relation
	 * on non-null object references:
	 * @apiNote
	 * It is generally necessary to override the {@link hashCode hashCode}
	 * method whenever this method is overridden, so as to maintain the
	 * general contract for the {@code hashCode} method, which states
	 * that equal objects must have equal hash codes.
	 * @param obj the reference object with which to compare.
	 * @return {@code true} if this object is the same as the obj
	 * argument; {@code false} otherwise.
	 * @see #hashCode()
	 * @see java.util.HashMap
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}

		if (obj == this)
		{
			return true;
		}

		if (obj instanceof Iterable<?>)
		{
			Iterable<?> col = (Iterable<?>) obj;
			Iterator<?> it2 = col.iterator();
			Iterator<E> it1 = iterator();
			while (it1.hasNext() && it2.hasNext())
			{
				if (!it1.next().equals(it2.next()))
				{
					return false;
				}
			}
			return it1.hasNext() == it2.hasNext();
		}
		return false;
	}
}
