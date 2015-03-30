package com.devoler.aicup.host.model.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ImmutableSet<T> implements Iterable<T> {
	private final Object[] elements;

	public ImmutableSet() {
		elements = new Object[0];
	}

	public ImmutableSet(Collection<T> collection) {
		int size = collection.size();
		elements = new Object[size];
		int index = 0;
		for (T element : collection) {
			elements[index++] = element;
		}
	}

	private ImmutableSet(Object[] elements) {
		this.elements = elements;
	}

	private int indexOf(T element) {
		for (int i = 0; i < elements.length; i++) {
			if (elements[i].equals(element)) {
				return i;
			}
		}
		return -1;
	}

	public boolean contains(T element) {
		return indexOf(element) >= 0;
	}

	public ImmutableSet<T> add(T newElement) {
		if (newElement == null) {
			throw new NullPointerException("Trying to add null element to immutable collection");
		}
		if (contains(newElement)) {
			return this;
		}
		Object[] newElements = new Object[elements.length + 1];
		System.arraycopy(elements, 0, newElements, 0, elements.length);
		newElements[elements.length] = newElement;
		return new ImmutableSet<>(newElements);
	}

	public ImmutableSet<T> replace(T oldElement, T newElement) {
		if ((oldElement != null) && (oldElement.equals(newElement))) {
			return this;
		}
		int index = indexOf(oldElement);
		if (index < 0) {
			return this;
		}
		if ((newElement == null) || (contains(newElement))) {
			return remove(oldElement);
		}
		Object[] newElements = new Object[elements.length];
		System.arraycopy(elements, 0, newElements, 0, elements.length);
		newElements[index] = newElement;
		return new ImmutableSet<>(newElements);
	}

	public ImmutableSet<T> remove(T element) {
		int index = indexOf(element);
		if (index < 0) {
			return this;
		}
		Object[] newElements = new Object[elements.length - 1];
		System.arraycopy(elements, 0, newElements, 0, index);
		System.arraycopy(elements, index + 1, newElements, index, elements.length - index - 1);
		return new ImmutableSet<>(newElements);
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			int pointer = 0;

			@Override
			public boolean hasNext() {
				return pointer < elements.length;
			}

			@Override
			@SuppressWarnings("unchecked")
			public T next() {
				if (pointer >= elements.length) {
					throw new NoSuchElementException();
				}
				pointer++;
				return (T) elements[pointer - 1];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ImmutableSet))
			return false;

		Iterator<T> e1 = iterator();
		Iterator e2 = ((ImmutableSet) o).iterator();
		while (e1.hasNext() && e2.hasNext()) {
			T o1 = e1.next();
			Object o2 = e2.next();
			if (!o1.equals(o2)) {
				return false;
			}
		}
		return !(e1.hasNext() || e2.hasNext());
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (T element : this)
			hashCode = 31 * hashCode + element.hashCode();
		return hashCode;
	}

	@Override
	public String toString() {
		Iterator<T> it = iterator();
		if (!it.hasNext())
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (;;) {
			T e = it.next();
			sb.append(e == this ? "(this Collection)" : e);
			if (!it.hasNext())
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}
}
