package net.thisptr.math.operator;

import net.thisptr.math.vector.DenseArrayVector;
import net.thisptr.math.vector.Vector;
import net.thisptr.math.vector.VectorVisitor;

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
	public static double dot(final Vector x, final double[] y, final int yOffset, final int yLength) {
		final double[] result = new double[1];
		x.walk(new VectorVisitor() {
			public void visit(final int index, final double value) {
				if (0 <= index && index < yLength)
					result[0] += y[index + yOffset] * value;
			}
		});
		return result[0];
	}
	
	/**
	 * Computes a squared L2-norm of a vector.
	 * @param x
	 * @return
	 */
	public static double l2norm2(final Vector x) {
		final double[] result = new double[1];
		x.walk(new VectorVisitor() {
			public void visit(final int index, final double value) {
				result[0] += value * value;
			}
		});
		return result[0];
	}
	
	/**
	 * Computes a L2-norm of a vector.
	 * @param x
	 * @return
	 */
	public static double l2norm(final Vector x) {
		return Math.sqrt(l2norm2(x));
	}

	public static double dot(DenseArrayVector v1, DenseArrayVector v2) {
		final double[] b1 = v1.raw();
		final double[] b2 = v2.raw();
		return ArrayOp.dot(b1, b2);
	}
}
