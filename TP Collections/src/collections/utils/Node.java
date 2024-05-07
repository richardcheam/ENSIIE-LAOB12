package collections.utils;
/**
 * Double linked Node of LinkedList with previous and next Nodes
 * @author davidroussel
 * @param <E> the type of content of this node
 */
public class Node<E>
{
	/**
	 * Data contained in this list
	 */
	private E data;
	
	/**
	 * Next node.
	 * Default value is null
	 */
	private Node<E> next;

	/**
	 * Previous node.
	 * Default value is null
	 */
	private Node<E> previous;

	/**
	 * Valued constructor
	 * @param data the data to set in this node
	 * @param previous the previous node to set
	 * @param next the next node to set
	 */
	public Node(E data, Node<E> previous, Node<E> next)
	{
		this.data = data;
		this.previous = previous;
		this.next = next;
	}

	/**
	 * Construtor with data only.
	 * {@link #previous} and {@link #next} Links are set to null
	 * @param data the data to set
	 */
	public Node(E data)
	{
		this(data, null, null);
	}

	/**
	 * Data accessor
	 * @return the data
	 */
	public E getData()
	{
		return data;
	}

	/**
	 * Indicator of non null next Link
	 * @return true if {@link #next} is non null, false otherwise
	 */
	public boolean hasNext()
	{
		return next != null;
	}

	/**
	 * Next Link accessor
	 * @return the next node ({@link #dnext} rather than {@link Node#next}
	 */
	public Node<E> getNext()
	{
		return next;
	}

	/**
	 * Next Link mutator
	 * @param next the next to set
	 */
	public void setNext(Node<E> next)
	{
		this.next = next;
	}

	/**
	 * Indicator of non null previous Link
	 * @return true if {@link #previous} is non null, false otherwise
	 */
	public boolean hasPrevious()
	{
		return previous != null;
	}

	/**
	 * Previous Link accessor
	 * @return the previous
	 */
	public Node<E> getPrevious()
	{
		return previous;
	}

	/**
	 * Previous Link mutator
	 * @param previous the previous to set
	 */
	public void setPrevious(Node<E> previous)
	{
		this.previous = previous;
	}

	/**
	 * Unlink this node by setting both {@link #previous} and {@link #next} to null
	 * @implNote Useful to clean up Nodes for the Garbage Collector
	 */
	public void unlink()
	{
		previous = null;
		next = null;
	}

	/**
     * Returns a string representation of this Node for debug purposes.
     * @return a string representation of this Node.
     */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (hasPrevious())
		{
			sb.append("<-");
		}
		sb.append(data.toString());
		if (hasNext())
		{
			sb.append("->");
		}
		return sb.toString();
	}
}
