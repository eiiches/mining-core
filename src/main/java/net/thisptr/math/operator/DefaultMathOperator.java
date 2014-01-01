package net.thisptr.math.operator;

import net.thisptr.math.matrix.Matrix;
import net.thisptr.math.vector.Vector;

public class DefaultMathOperator implements MathOperator {
	@Override
	public double dot(final Vector v1, final Vector v2) {
		assert v1.size() == v2.size();

		double result = 0;
		for (int i = 0; i < v1.size(); ++i)
			result = v1.get(i) * v2.get(i);
		return result;
	}

	public void assignMultiply(final Vector result, final Matrix x, final Vector y) {
		assert result.size() == y.size();
		assert x.columns() == y.size();

		for (int i = 0; i < x.rows(); ++i)
			result.set(i, dot(x.row(i), y));
	}

	public void assignMultiply(final Matrix result, final Matrix x, final Matrix y) {
		assert result.columns() == y.columns();
		assert result.rows() == x.rows();
		assert x.columns() == y.rows();

		for (int j = 0; j < result.columns(); ++j)
			assignMultiply(result.column(j), x, y.column(j));
	}
}
