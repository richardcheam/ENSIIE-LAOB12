package collections;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import collections.utils.Node;

public class MyLinkedCollection<E> extends AbstractCollection<E> {

	private Node<E> head;
	
	public MyLinkedCollection() {
		head = null;
	}
	
	public MyLinkedCollection(Collection<E> col) {
		this();
		for(E elt : col) {
			add(elt);
		}
	}
	
	@Override
	public boolean add(E e) throws NullPointerException
	{
		if(e == null) {
			throw new NullPointerException("null object");
		}
		Node<E> current = head;
		Node<E> previous = null;
		while(current != null) {
			previous = current;
			current = current.getNext();
		}
		Node<E> added = new Node<E>(e, previous, null);
		if(previous != null) {
			previous.setNext(added);
		}
		else {
			head = added;
		}
		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return new NodeIterator();
	}

	@Override
	public int size() {
		Node<E> current = head;
		int count = 0;
		while(current != null) {
			current = current.getNext();
			count++;
		}
		return count;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 1;
		for (E elt : this) {
			hash = prime * hash + (elt == null ? 0 : elt.hashCode());
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Iterable<?>)) {
			return false;
		}
		Iterable<?> col = (Iterable<?>) obj;
		Iterator<E> it1 = iterator();
		Iterator<?> it2 = col.iterator();
		while(it1.hasNext() && it2.hasNext()) {
			if(!it1.next().equals(it2.next())) {
				return false;
			}
		}
		return it1.hasNext() == it2.hasNext();
	}
	private class NodeIterator implements Iterator<E> {

		private Node<E> current;
		private Node<E> previous;
		private boolean nextCalled;
		
		public NodeIterator() {
			current = head;
			previous = null;
			nextCalled = false;
		}
		
		@Override
		public boolean hasNext() {
			return (current != null);
		}

		@Override
		public E next() throws NoSuchElementException{
			// TODO Auto-generated method stub
			if(!hasNext()) {
				throw new NoSuchElementException("no more elements");
			}
			previous = current;
			E data = current.getData();
			current = current.getNext();
			nextCalled = true;
			return data;
		}

		@Override
		public void remove() throws IllegalStateException{
			if(!nextCalled) {
				throw new IllegalStateException("next has not been called");
			}
			if(previous == null) {
				throw new IllegalStateException("can't remove null previous");
			}
			if(previous != head) {
				Node<E> penultimate = previous.getPrevious();
				if (current != null) {
					current.setPrevious(penultimate);
				}
				if (penultimate != null) {
					penultimate.setNext(current);
				}
				else {
					throw new IllegalStateException("null penultimate");
				}
			}
			else {
				head = head.getNext();
				if(head != null) {
					head.setPrevious(null);
				}
			}
			previous.unlink();
			previous = null;
			nextCalled = false;
		}
		
		

	}
}
