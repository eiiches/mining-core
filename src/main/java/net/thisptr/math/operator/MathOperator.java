package net.thisptr.math.operator;

import net.thisptr.math.matrix.Matrix;
import net.thisptr.math.vector.Vector;

public interface MathOperator {
	/**
	 * result = x * y
	 * @param result a column vector
	 * @param x a matrix
	 * @param y a column vector
	 */
	void assignMultiply(Vector result, Matrix x, Vector y);

	/**
	 * result = x * y
	 * @param result a matrix
	 * @param x a matrix
	 * @param y a matrix
	 */
	void assignMultiply(Matrix result, Matrix x, Matrix y);

	/**
	 * Returns a dot product (inner product) of v1 and v2.
	 * 
	 * @param v1 a column vector
	 * @param v2 a column vector
	 * @return a value of the dot product
	 */
	double dot(Vector v1, Vector v2);
}
