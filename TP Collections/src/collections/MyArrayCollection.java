package collections;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import collections.utils.Capacity;

public class MyArrayCollection<E> extends AbstractCollection<E> implements Capacity<E> {
	
	private E[] array;
	private int size;
	private int capacity;
	private int capacityIncrement;
	
	@SuppressWarnings("unchecked")
	public MyArrayCollection(int c1, int c2) throws IllegalArgumentException {
		if (c1 < 0) {
			throw new IllegalArgumentException("negative capacity");
		}
		if (c2 <= 0) {
			throw new IllegalArgumentException("negative or null increment capcity");
		}
		capacity = c1;
		capacityIncrement = c2;
		size = 0;
		E[] es = (E[])new Object[capacity];
		array = es;
		
	}
	
	public MyArrayCollection(int initialCapacity) {
		this(initialCapacity, Capacity.DEFAULT_CAPACITY_INCREMENT);
	}
	
	public MyArrayCollection() {	
		this(Capacity.DEFAULT_CAPACITY, Capacity.DEFAULT_CAPACITY_INCREMENT);
	}
	
	public MyArrayCollection(Collection<E> col) {
		this(col.size(), Capacity.DEFAULT_CAPACITY_INCREMENT);
		for(E elt : col) {
			add(elt);
		}
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public int getCapacityIncrement() {
		return capacityIncrement;
	}

	@Override
	public void grow(int amount) throws IllegalArgumentException {
		if(amount < 0) {
			throw new IllegalArgumentException("negative amount");
		}
		capacity += amount;
		array = Capacity.resizeArray(array, capacity);
	}

	@Override
	public boolean add(E e) {
		if (e == null) {
			throw new NullPointerException("null element");
		}
		//check required growth
		if (size == capacity) {
			grow(capacityIncrement);
		}
		//add element into array
		size++;
		array[size - 1] = e;
		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return new ArrayIterator();
	}

	@Override
	public int size() {
		return size;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 1;
		for (E elt : this) {
			hash = (prime * hash) + (elt == null ? 0 : elt.hashCode());
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


	public class ArrayIterator implements Iterator<E> {
		
		private int index;
		private boolean nextCalled;
		
		public ArrayIterator() {
			index = 0;
			nextCalled = false;
		}

		@Override
		public boolean hasNext() {
			return index < size;
		}

		@Override
		public E next() {
			if(!hasNext()) {
				throw new NoSuchElementException("no more elements");
			}
			nextCalled = true;
			return array[index++];
		}

		@Override
		public void remove() {
			if(!nextCalled) {
				throw new IllegalStateException("next has not been called");
			}
			if(index <= 0) {
				throw new IllegalStateException("invalid index");
			}
			for(int i = index - 1; i < (size - 1); i++) {
				array[i] = array[i + 1];
			}
			size--;
			index--;
			nextCalled = false;
		}

	}

}
