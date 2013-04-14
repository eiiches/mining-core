package net.thisptr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import net.thisptr.math.operation.ArrayOp;

public final class SelectUtils {
	private SelectUtils() { }
	
	public static int random(final int size) {
		// We can't draw an element out of empty array.
		assert size > 0;
		
		final Random random = new Random();
		return random.nextInt(size);
	}
	
	public static int[] random(final int size, final int n) {
		return random(size, n, false);
	}
	
	public static int[] random(final int size, final int n, final boolean allowDuplicate) {
		// We can't select n elements from array smaller than n.
		assert !allowDuplicate && n <= size;
		
		final Random random = new Random();
		final int[] result = new int[n];
		
		if (allowDuplicate) {
			for (int i = 0; i < n; ++i) {
				final int r = random.nextInt(size);
				result[i] = r;
			}
		} else {
			final boolean[] used = new boolean[size];
			for (int i = 0; i < n; ++i) {
				int r = random.nextInt(size - i);
				
				// search for r-th open index
				int j = -1;
				do {
					++j;
					while (used[j]) ++j;
				} while (r-- > 0);
				
				used[j] = true;
				result[i] = j;
			}
		}
		
		return result;
	}
	
	public static int roulette(final double[] scores) {
		return roulette(scores, null);
	}
	
	private static int roulette(final double[] scores, final boolean[] exclude) {
		assert exclude == null || exclude.length == scores.length;
		final int n = scores.length;
		
		final double[] maskedScores = exclude != null
				? ArrayOp.maskNew(scores, exclude)
				: scores;
		
		final double sumScores = ArrayOp.sum(maskedScores);
		
		final double r = Math.random();
		
		double sump = 0.0;
		for (int i = 0; i < n; ++i) {
			final double p = maskedScores[i] / sumScores;
			sump += p;
			if (r < sump)
				return i;
		}
		
		/* should not reach here */
		throw new NoSuchElementException();
	}
	
	/**
	 * Run a roulette selection using given scores. This function does not return duplicate indices.
	 * @param scores
	 * @param n
	 * @return
	 */
	public static int[] roulette(final double[] scores, final int n) {
		return roulette(scores, n, false);
	}

	/**
	 * @param scores
	 * @param n
	 * @param allowDuplicate
	 * @return
	 * @throws NoSuchElementException if the number of <tt>items</tt> is below <tt>n</tt>.
	 */
	public static int[] roulette(final double[] scores, final int n, final boolean allowDuplicate) {
		final int[] result = new int[n];

		final boolean[] used = new boolean[scores.length];

		for (int i = 0; i < n; ++i) {
			final int index = SelectUtils.roulette(scores, allowDuplicate ? null : used);
			result[i] = index;
			used[index] = true;
		}

		return result;
	}
	
	/**
	 * @param scores
	 * @return
	 * @throws IndexOutOfBoundsException if scores.length == 0.
	 */
	public static int best(final double[] scores) {
		return best(scores, 1)[0];
	}
	
	/**
	 * @param scores
	 * @param n
	 * @return array containing indices of the n-best values.
	 */
	public static int[] best(final double[] scores, final int n) {
		final int nn = scores.length < n ? scores.length : n;
			
		final int[] result = new int[nn];
		
		final boolean[] used = new boolean[scores.length];
		for (int i = 0; i < nn; ++i) {
			int maxj = -1;
			for (int j = 0; j < scores.length; ++j)
				if (!used[j] && (maxj < 0 || scores[maxj] < scores[j]))
					maxj = j;
			used[maxj] = true;
			result[i] = maxj;
		}
		
		return result;
	}
	
	public static <T> List<T> toItems(final List<T> items, final int[] indices) {
		final List<T> result = new ArrayList<T>(indices.length);
		for (final int index : indices)
			result.add(items.get(index));
		return result;
	}
	
	public static <T> List<ScoredItem<T>> toItems(final List<T> items, final double[] scores, final int[] indices) {
		final List<ScoredItem<T>> result = new ArrayList<ScoredItem<T>>(indices.length);
		for (final int index : indices)
			result.add(new ScoredItem<T>(items.get(index), scores[index]));
		return result;
	}
}