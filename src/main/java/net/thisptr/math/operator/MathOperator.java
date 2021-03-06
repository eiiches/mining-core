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
	 * result = x * y
	 * @param result a matrix
	 * @param x a matrix
	 * @param y a matrix
	 */
	void assignMultiply(Matrix result, Matrix x, Matrix y, double s);

	/**
	 * result = x * s
	 * @param result
	 * @param x
	 * @param s
	 */
	void assignMultiply(Matrix result, Matrix x, double s);

	/**
	 * Returns a dot product (inner product) of v1 and v2.
	 * 
	 * @param v1 a column vector
	 * @param v2 a column vector
	 * @return a value of the dot product
	 */
	double dot(Vector v1, Vector v2);

	void assignZero(Vector v);

	void assignZero(Matrix v);

	/**
	 * self += x * s
	 * @param self
	 * @param x
	 * @param s
	 */
	void addMultiply(Matrix self, Matrix x, double s);

	/**
	 * self += x * y * s
	 * @param self
	 * @param x
	 * @param y
	 * @param s
	 */
	void addMultiply(Matrix self, Matrix x, Matrix y, double s);

	/**
	 * self += x
	 * @param self
	 * @param x
	 */
	void add(Matrix self, Matrix x);

	void copyElements(Vector dest, int destIndex, Vector src, int srcIndex, int count);

	double l1Norm(Matrix m);

	double l2Norm(Matrix m);
}
