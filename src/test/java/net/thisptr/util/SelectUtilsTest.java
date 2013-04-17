package net.thisptr.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import net.thisptr.math.operator.ArrayOp;

import org.junit.Test;

public class SelectUtilsTest {
	
	@Test
	public void testRoulette() {
		final double[] scores = new double[] { 1.0, 2.0, 4.0, 3.0 };
		final double sumScores = ArrayOp.sum(scores);
		
		final double[] counts = new double[scores.length];
		for (int i = 0; i < 20000; ++i) {
			final int index = SelectUtils.roulette(scores);
			++counts[index];
		}
		final double sumCounts = ArrayOp.sum(counts);
		
		// normalize to test if to elements of the arrays are similar.
		ArrayOp.div(counts, sumCounts);
		ArrayOp.div(scores, sumScores);
		
		assertArrayEquals(counts, scores, 0.01);
	}
	
	@Test
	public void testBest1() {
		final double[] scores = new double[] { 1.0, 2.0, 4.0, 3.0 };
		assertEquals(2, SelectUtils.best(scores));
	}
	
	@Test
	public void testBest2() {
		final double[] scores = new double[] { 1.0, 2.0, 4.0, 3.0 };
		assertArrayEquals(new int[] { 2, 3 }, SelectUtils.best(scores, 2));
	}
	
	@Test
	public void testRandom1() {
		final int[] actual = SelectUtils.random(3, 3);
		Arrays.sort(actual);
		
		assertArrayEquals(new int[] { 0, 1, 2 }, actual);
	}
	
	@Test
	public void testRandom2() {
		final double[] counts = new double[5];
		
		for (int i = 0; i < 20000; ++i) {
			final int[] indices = SelectUtils.random(counts.length, 2, false);
			for (final int index : indices)
				++counts[index];
		}
		
		ArrayOp.div(counts, ArrayOp.sum(counts));
		
		for (final double count : counts)
			assertEquals(1.0 / counts.length, count, 0.01);
	}
}
