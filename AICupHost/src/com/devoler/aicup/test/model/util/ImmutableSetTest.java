package com.devoler.aicup.test.model.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

import com.devoler.aicup.host.model.util.ImmutableSet;

public class ImmutableSetTest {
	@SafeVarargs
	private static final <T> ImmutableSet<T> createSet(T... elements) {
		return new ImmutableSet<>(Arrays.asList(elements));
	}

	@Test
	public void testAdd() {
		ImmutableSet<Integer> emptySet = new ImmutableSet<>();
		try {
			emptySet.add(null);
			fail("NPE not thrown");
		} catch (NullPointerException expected) {
		}
		ImmutableSet<Integer> one = emptySet.add(1);
		assertEquals(createSet(1), one);
		ImmutableSet<Integer> oneTwo = one.add(2);
		assertEquals(createSet(1, 2), oneTwo);
		assertEquals(createSet(1, 2), oneTwo.add(2));
		assertEquals(createSet(1, 2, 3), oneTwo.add(3));
	}

	@Test
	public void testRemove() {
		ImmutableSet<Integer> oneTwoThree = createSet(1, 2, 3);
		assertEquals(oneTwoThree, oneTwoThree.remove(null));
		assertEquals(oneTwoThree, oneTwoThree.remove(4));
		assertEquals(createSet(1, 2), oneTwoThree.remove(3));
		assertEquals(createSet(1, 3), oneTwoThree.remove(2));
		assertEquals(createSet(3), oneTwoThree.remove(2).remove(1));
	}

	@Test
	public void testReplace() {
		ImmutableSet<Integer> oneTwoThree = createSet(1, 2, 3);
		assertEquals(oneTwoThree, oneTwoThree.replace(1, 1));
		assertEquals(oneTwoThree, oneTwoThree.replace(null, 4));
		assertEquals(createSet(1, 2), oneTwoThree.replace(3, 1));
		assertEquals(createSet(1, 2), oneTwoThree.replace(3, null));
		assertEquals(createSet(1, 4, 3), oneTwoThree.replace(2, 4));
	}

	@Test
	public void testContains() {
		ImmutableSet<Integer> oneTwoThree = createSet(1, 2, 3);
		assertEquals(true, oneTwoThree.contains(1));
		assertEquals(true, oneTwoThree.contains(2));
		assertEquals(true, oneTwoThree.contains(3));
		assertEquals(false, oneTwoThree.contains(4));
		assertEquals(false, oneTwoThree.contains(null));
	}

	@Test
	public void testIterator() {
		ImmutableSet<Integer> oneTwoThree = createSet(1, 2, 3);
		Iterator<Integer> i = oneTwoThree.iterator();
		oneTwoThree.remove(2);
		oneTwoThree.replace(1, 5);
		assertEquals(1, i.next().intValue());
		assertEquals(2, i.next().intValue());
		assertEquals(3, i.next().intValue());
	}
}
