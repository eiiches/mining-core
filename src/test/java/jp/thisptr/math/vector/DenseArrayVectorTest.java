package jp.thisptr.math.vector;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.thisptr.lang.tuple.Pair;

import org.junit.Before;
import org.junit.Test;

public class DenseArrayVectorTest {
	private Vector sut = null;

	@Before
	public void setUp() {
		sut = new DenseArrayVector(0.0, 1.0, 0.0, 2.0);
	}
	
	private static void sortElements(final List<Pair<Integer, Double>> elements) {
		Collections.sort(elements, new Comparator<Pair<Integer, Double>>() {
			@Override
			public int compare(final Pair<Integer, Double> o1, final Pair<Integer, Double> o2) {
				return o1.getFirst().compareTo(o2.getFirst());
			}
		});
	}

	@Test
	public void testCapacity() {
		assertEquals(4, sut.capacity());
	}
	
	@Test
	public void testSize() {
		assertEquals(4, sut.size());
	}
	
	@Test
	public void testSet() {
		sut.set(0, 3.0);
		assertEquals(3.0, sut.get(0), 0.0);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testOutOfBoundSet() {
		sut.set(4, 3.0);
	}

	@Test
	public void testGet() {
		assertEquals(0.0, sut.get(0), 0.0);
		assertEquals(1.0, sut.get(1), 0.0);
		assertEquals(0.0, sut.get(2), 0.0);
		assertEquals(2.0, sut.get(3), 0.0);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testOutOfBoundGet() {
		sut.get(4);
	}
	
	@Test
	public void testWalk() {
		// Vector#accept() does not specify the order they visit.
		final List<Pair<Integer, Double>> actual = new ArrayList<>();
		sut.walk(new Vector.Visitor() {
			public void visit(final int index, final double value) {
				actual.add(Pair.make(index, value));
			}
		});
		sortElements(actual);
		assertEquals(Arrays.asList(Pair.make(1, 1.0), Pair.make(3, 2.0)), actual);
	}
	
	@Test
	public void testIterator() {
		// Vector#iterator() does not specify the order of iteration.
		final List<Pair<Integer, Double>> actual = new ArrayList<>();
		for (final Vector.Element e : sut)
			actual.add(Pair.make(e.index(), e.value()));
		sortElements(actual);
		assertEquals(Arrays.asList(Pair.make(1, 1.0), Pair.make(3, 2.0)), actual);
	}
}
