package net.thisptr.math.vector;

import net.thisptr.math.matrix.Matrix;

public interface Vector extends Matrix {
	int size();

	int capacity();

	void resize(int size);

	public VectorShape shape();

	/**
	 * Get value at the given index.
	 * 
	 * @param index
	 * @return value at the given index
	 */
	double get(final int index);

	/**
	 * Set value at the given index.
	 * 
	 * @param index
	 * @param value
	 * @throws UnsupportedOperationException
	 *             if this vector does not support modification.
	 */
	void set(final int index, final double value);

	/**
	 * Visit each non-zero element in this vector. Note that the order of visits is not specified.
	 * 
	 * @param visitor
	 * @return a value returned from {@link VectorVisitor#finish()}
	 */
	double walk(final VectorVisitor visitor);
}