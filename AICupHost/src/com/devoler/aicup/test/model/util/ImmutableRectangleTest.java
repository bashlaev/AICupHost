package com.devoler.aicup.test.model.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.devoler.aicup.host.model.util.ImmutableRectangle;

public class ImmutableRectangleTest {

	@Test
	public void testContains() {
		assertTrue(new ImmutableRectangle(0, 0, 1, 1).contains(new ImmutableRectangle(0, 0, 1, 1)));
		assertTrue(new ImmutableRectangle(0, 0, 2, 1).contains(new ImmutableRectangle(0, 0, 1, 1)));
		assertTrue(new ImmutableRectangle(0, 0, 1, 2).contains(new ImmutableRectangle(0, 0, 1, 1)));
		assertTrue(new ImmutableRectangle(0, 0, 2, 2).contains(new ImmutableRectangle(0, 0, 1, 1)));
		assertFalse(new ImmutableRectangle(0, 0, 1, 1).contains(new ImmutableRectangle(0, 0, 2, 1)));
		assertFalse(new ImmutableRectangle(0, 0, 0, 1).contains(new ImmutableRectangle(0, 0, 1, 1)));
		assertFalse(new ImmutableRectangle(0, 0, 1, 0).contains(new ImmutableRectangle(0, 0, 1, 1)));
		assertFalse(new ImmutableRectangle(0, 0, 1, 1).contains(new ImmutableRectangle(1, 0, 1, 1)));
		assertTrue(new ImmutableRectangle(0, 0, 2, 2).contains(new ImmutableRectangle(1, 1, 1, 1)));
	}

	@Test
	public void testGetManhattanDistance() {
		assertEquals(0, new ImmutableRectangle(0, 0, 2, 2).getManhattanDistance(0, 0));
		assertEquals(1, new ImmutableRectangle(0, 0, 2, 2).getManhattanDistance(0, 2));
		assertEquals(1, new ImmutableRectangle(0, 0, 2, 2).getManhattanDistance(2, 0));
		assertEquals(2, new ImmutableRectangle(0, 0, 2, 2).getManhattanDistance(2, 2));
		assertEquals(0, new ImmutableRectangle(0, 0, 2, 2).getManhattanDistance(1, 1));
		assertEquals(2, new ImmutableRectangle(0, 0, 2, 2).getManhattanDistance(-1, -1));
		assertEquals(4, new ImmutableRectangle(0, 0, 2, 2).getManhattanDistance(3, 3));
	}

	@Test
	public void testIntersects() {
		assertTrue(new ImmutableRectangle(0, 0, 1, 1).intersects(new ImmutableRectangle(0, 0, 1, 1)));
		assertFalse(new ImmutableRectangle(0, 0, 1, 1).intersects(new ImmutableRectangle(1, 1, 1, 1)));
		assertFalse(new ImmutableRectangle(0, 0, 1, 1).intersects(new ImmutableRectangle(0, 1, 1, 1)));
		assertFalse(new ImmutableRectangle(0, 0, 1, 1).intersects(new ImmutableRectangle(1, 0, 1, 1)));
	}

	@Test
	public void testContainsIntInt() {
		assertTrue(new ImmutableRectangle(0, 0, 1, 1).contains(0, 0));
		assertFalse(new ImmutableRectangle(0, 0, 1, 1).contains(0, 1));
		assertFalse(new ImmutableRectangle(0, 0, 1, 1).contains(1, 0));
		assertFalse(new ImmutableRectangle(0, 0, 1, 1).contains(1, 1));
	}

}
