package jp.thisptr.math.vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class DenseArrayVectorTest {
	@Test
	public void testIterator() {
		final DenseArrayVector v = new DenseArrayVector(0.0, 1.0, 0.0, 1.0);
		final Iterator<Vector.Element> iter = v.iterator();
		
		assertTrue(iter.hasNext());
		final Vector.Element e1 = iter.next();
		assertEquals(1, e1.index());
		assertEquals(1.0, e1.value(), 0.0);
		
		assertTrue(iter.hasNext());
		final Vector.Element e2 = iter.next();
		assertEquals(3, e2.index());
		assertEquals(1.0, e2.value(), 0.0);
		
		assertFalse(iter.hasNext());
		try {
			iter.next();
			fail("At the end of the element, NoSuchElementException should be thrown");
		} catch (NoSuchElementException e) { }
	}
}
