package jp.thisptr.math.structure.operation;

import java.util.Map;

import jp.thisptr.math.structure.vector.SparseMapVector;

public final class VectorOp {
	private VectorOp() { }
	
	/**
	 * Computes a dot product.
	 * @param x A vector.
	 * @param y A vector against to which to take the dot product of {@code x}.
	 * @param yOffset An offset of {@code y}.
	 * @param yLength A length of {@code y}, staring from the offset.
	 * @return The dot product of {@code x} and {@code y}.
	 */
	public static double dot(final SparseMapVector x, final double[] y, final int yOffset, final int yLength) {
		double result = 0.0;
		for (final Map.Entry<Integer, Double> xi : x.rawMap().entrySet()) {
			final int i = xi.getKey();
			if (0 <= i && i < yLength)
				result += y[i + yOffset] * xi.getValue();
		}
		return result;
	}
	
	/**
	 * Computes a squared L2-norm of a vector.
	 * @param x
	 * @return
	 */
	public static double l2norm2(final SparseMapVector x) {
		double result = 0.0;
		for (final Map.Entry<Integer, Double> xi : x.rawMap().entrySet()) {
			final double value = xi.getValue();
			result += value * value;
		}
		return result;
	}
	
	/**
	 * Computes a L2-norm of a vector.
	 * @param x
	 * @return
	 */
	public static double l2norm(final SparseMapVector x) {
		return Math.sqrt(l2norm2(x));
	}
}
