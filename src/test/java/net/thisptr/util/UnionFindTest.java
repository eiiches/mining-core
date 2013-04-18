package net.thisptr.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class UnionFindTest {
	public static class SimpleTest {
		private UnionFind sut = new UnionFind(8);
		
		@Before
		public void setUp() {
			sut.unite(0, 1);
			sut.unite(2, 3);
			
			// reallocation of arrays will be needed internally.
			sut.unite(3, 8);
		}
		
		@Test
		public void testFind() {
			assertEquals(0, sut.find(1));
			assertEquals(0, sut.find(1));
			assertEquals(2, sut.find(2));
			assertEquals(2, sut.find(3));
			assertEquals(2, sut.find(8));
			
			// not-united elements
			assertEquals(4, sut.find(4));
			
			// out of bounds access
			assertEquals(32, sut.find(32));
		}
	
		@Test
		public void testIsUnion() {
			assertFalse(sut.isUnion(0, 2));
			assertFalse(sut.isUnion(0, 3));
			assertFalse(sut.isUnion(1, 2));
			assertFalse(sut.isUnion(1, 3));
			assertTrue(sut.isUnion(0, 1));
			assertTrue(sut.isUnion(2, 3));
			assertTrue(sut.isUnion(2, 8));
			
			// not-united elements
			assertFalse(sut.isUnion(4, 3));
			assertFalse(sut.isUnion(4, 0));
			
			// out of bounds access
			assertFalse(sut.isUnion(4, 32));
		}
	}
}
