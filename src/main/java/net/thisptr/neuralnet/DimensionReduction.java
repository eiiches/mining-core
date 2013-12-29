package net.thisptr.neuralnet;

import net.thisptr.math.vector.Vector;

public interface DimensionReduction {
	Vector reduce(final Vector vector);

	/**
	 * @param h
	 * @return the recovered high-dimensional vector
	 * @throws UnsupportedOperationException if the class does not support recovering a high-dimensional vector.
	 */
	Vector reconstruct(final Vector h);
}
