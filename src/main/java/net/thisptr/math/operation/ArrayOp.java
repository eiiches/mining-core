package net.thisptr.math.operation;

import java.util.Arrays;

public final class ArrayOp {
	private ArrayOp() { }
	
	public static double[] sub(final double[] self, final double[] v) {
		for (int i = 0; i < self.length; ++i)
			self[i] -= v[i];
		return self;
	}
	
	public static double[] sub(final double[] result, final double[] left, final double[] right) {
		for (int i = 0; i < result.length; ++i)
			result[i] = left[i] - right[i];
		return result;
	}
	
	public static double[] subNew(final double[] left, final double[] right) {
		final double[] result = new double[left.length];
		sub(result, left, right);
		return result;
	}
	
	public static double[] subScaled(final double[] self, final double scale, final double[] v) {
		for (int i = 0; i < self.length; ++i)
			self[i] -= v[i] * scale;
		return self;
	}
	
	public static void add(final double[] result, final double[] value) {
		for (int i = 0; i < result.length; ++i)
			result[i] += value[i];
	}
	
	public static void add(final double[] result, final double[] left, final double[] right) {
		for (int i = 0; i < result.length; ++i)
			result[i] = left[i] + right[i];
	}
	
	public static double[] addNew(final double[] left, final double[] right) {
		final double[] result = new double[left.length];
		add(result, left, right);
		return result;
	}
	
	public static double[] addScaled(final double[] self, final double scale, final double[] v) {
		for (int i = 0; i < self.length; ++i)
			self[i] += v[i] * scale;
		return self;
	}
	
	public static double[] addScaled(final double[] result, final double[] x, final double scale, final double[] y) {
		for (int i = 0; i < x.length; ++i)
			result[i] = x[i] + y[i] * scale;
		return result;
	}
	
	public static double[] mul(final double[] result, final double scale) {
		for (int i = 0; i < result.length; ++i)
			result[i] *= scale;
		return result;
	}
	
	public static double[] mul(final double[] result, final double[] v, final double s) {
		for (int i = 0; i < result.length; ++i)
			result[i] = v[i] * s;
		return result;
	}
	
	public static double[] mulNew(final double[] v, final double s) {
		final double[] result = new double[v.length];
		mul(result, v, s);
		return result;
	}
	
	public static double dot(final double[] v1, final double[] v2) {
		double result = 0.0;
		for (int i = 0; i < v1.length; ++i)
			result += v1[i] * v2[i];
		return result;
	}
	
	public static double l1norm(final double[] v1) {
		double result = 0.0;
		for (int i = 0; i < v1.length; ++i)
			result += v1[i];
		return result;
	}
	
	/**
	 * L1-norm of the vector,
	 * only considering the components where the corresponding mask component is true.
	 * @param v1
	 * @param mask
	 * @return
	 */
	public static double l1norm(final double[] v1, final boolean[] mask) {
		double result = 0.0;
		for (int i = 0; i < v1.length; ++i)
			if (mask[i])
				result += v1[i];
		return result;
	}
	
	public static double l2norm(final double[] v1) {
		double result = 0.0;
		for (int i = 0; i < v1.length; ++i)
			result += v1[i] * v1[i];
		return Math.sqrt(result);
	}

	public static double[] negate(final double[] self) {
		for (int i = 0; i < self.length; ++i)
			self[i] = -self[i];
		return self;
	}
	
	public static double[] negateNew(final double[] self) {
		final double[] result = new double[self.length];
		for (int i = 0; i < self.length; ++i)
			result[i] = -self[i];
		return result;
	}
	
	public static boolean isNaN(final double[] self) {
		for (int i = 0; i < self.length; ++i)
			if (Double.isNaN(self[i]))
				return true;
		return false;
	}

	public static int nonzero(final double[] self) {
		int count = 0;
		for (int i = 0; i < self.length; ++i)
			if (self[i] != 0)
				++count;
		return count;
	}
	
	public static int nonzero(final double[] self, final double criteria) {
		int count = 0;
		for (int i = 0; i < self.length; ++i)
			if (self[i] < criteria)
				++count;
		return count;
	}
	
	private static final int DEFAULT_TO_STRING_N = 5;
	
	public static String toString(final double[] self) {
		return toString(self, DEFAULT_TO_STRING_N);
	}
	
	public static String toString(final double[] self, final int n) {
		final StringBuilder builder = new StringBuilder("[");
		
		String sep = "";
		for (int i = 0; i < self.length; ++i) {
			if (i >= n) {
				builder.append(String.format("%s...", sep));
				break;
			}
			builder.append(String.format("%s%.3f", sep, self[i]));
			sep = ", ";
		}
		
		return builder.append("]").toString();
	}

	/**
	 * Computes a L-∞ norm (Finds the maximum absolute value of the components).
	 * @param x
	 * @return
	 */
	public static double absmax(final double[] x) {
		double result = 0.0;
		for (int i = 0; i < x.length; ++i) {
			final double abs = Math.abs(x[i]);
			if (abs > result)
				result = abs;
		}
		return result;
	}
	
	public static void fill(final double[] x, final double value) {
		Arrays.fill(x, value);
	}

	public static double[] fillNew(final double value, final int n) {
		final double[] result = new double[n];
		fill(result, value);
		return result;
	}
}
