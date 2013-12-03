package net.thisptr.util;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;


public class RangeTest {
	@Test
	public void testSlice() throws Exception {
		assertEquals(Arrays.asList(new Range(1, 2), new Range(2, 3)), new Range(1, 3).slice(1));
		assertEquals(Arrays.asList(new Range(1, 3)), new Range(1, 3).slice(2));
		assertEquals(Arrays.asList(new Range(1, 3)), new Range(1, 3).slice(3));
		assertEquals(Arrays.asList(), new Range(1, 1).slice(3));
	}

	@Test
	public void testSplit() throws Exception {
		assertEquals(Arrays.asList(new Range(1, 2), new Range(2, 3)), new Range(1, 3).split(2));
		assertEquals(Arrays.asList(new Range(1, 3), new Range(3, 4)), new Range(1, 4).split(2));
		assertEquals(Arrays.asList(new Range(1, 2), new Range(2, 3), new Range(3, 4)), new Range(1, 4).split(4));
		assertEquals(Arrays.asList(), new Range(1, 1).split(3));
	}
}
