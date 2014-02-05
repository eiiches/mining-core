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

	@Override
	public void assignMultiply(final Vector result, final Matrix x, final Vector y) {
		assert result.size() == y.size();
		assert x.columns() == y.size();

		for (int i = 0; i < x.rows(); ++i)
			result.set(i, dot(x.row(i), y));
	}

	@Override
	public void assignMultiply(final Matrix result, final Matrix x, final Matrix y, final double s) {
		assert result.columns() == y.columns();
		assert result.rows() == x.rows();
		assert x.columns() == y.rows();

		for (int i = 0; i < result.rows(); ++i) {
			for (int j = 0; j < result.columns(); ++j) {
				double t = 0.0;
				for (int k = 0; k < x.columns(); ++k)
					t += x.get(i, k) * y.get(k, j);
				result.set(i, j, t * s);
			}
		}
	}

	@Override
	public void assignMultiply(final Matrix result, final Matrix x, final Matrix y) {
		assert result.columns() == y.columns();
		assert result.rows() == x.rows();
		assert x.columns() == y.rows();

		for (int i = 0; i < result.rows(); ++i) {
			for (int j = 0; j < result.columns(); ++j) {
				double t = 0.0;
				for (int k = 0; k < x.columns(); ++k)
					t += x.get(i, k) * y.get(k, j);
				result.set(i, j, t);
			}
		}
	}

	@Override
	public void assignZero(final Vector v) {
		for (int i = 0; i < v.size(); ++i)
			v.set(i, 0.0);
	}

	@Override
	public void assignZero(final Matrix m) {
		for (int i = 0; i < m.rows(); ++i)
			for (int j = 0; j < m.columns(); ++j)
				m.set(i, j, 0.0);
	}

	@Override
	public void assignMultiply(final Matrix result, final Matrix x, final double s) {
		assert result.columns() == x.columns();
		assert result.rows() == x.rows();

		for (int i = 0; i < result.rows(); ++i)
			for (int j = 0; j < result.columns(); ++j)
				result.set(i, j, x.get(i, j) * s);
	}

	@Override
	public void addMultiply(final Matrix self, final Matrix x, final Matrix y, final double s) {
		assert self.columns() == y.columns();
		assert self.rows() == x.rows();
		assert x.columns() == y.rows();

		for (int i = 0; i < self.rows(); ++i) {
			for (int j = 0; j < self.columns(); ++j) {
				double t = 0.0;
				for (int k = 0; k < x.columns(); ++k)
					t += x.get(i, k) * y.get(k, j);
				self.set(i, j, t * s + self.get(i, j));
			}
		}
	}

	@Override
	public void addMultiply(final Matrix self, final Matrix x, final double s) {
		assert self.columns() == x.columns();
		assert self.rows() == x.rows();

		for (int i = 0; i < self.rows(); ++i)
			for (int j = 0; j < self.columns(); ++j)
				self.set(i, j, self.get(i, j) + x.get(i, j) * s);
	}

	@Override
	public void add(final Matrix self, final Matrix x) {
		assert self.columns() == x.columns();
		assert self.rows() == x.rows();

		for (int i = 0; i < self.rows(); ++i)
			for (int j = 0; j < self.columns(); ++j)
				self.set(i, j, self.get(i, j) + x.get(i, j));
	}

	@Override
	public void copyElements(final Vector dest, final int destIndex, final Vector src, final int srcIndex, final int count) {
		for (int i = 0; i < count; ++i)
			dest.set(destIndex + i, src.get(srcIndex + i));
	}

	@Override
	public double l1Norm(final Matrix m) {
		double sum = 0.0;
		for (int i = 0; i < m.rows(); ++i)
			for (int j = 0; j < m.columns(); ++j)
				sum += Math.abs(m.get(i, j));
		return sum;
	}

	@Override
	public double l2Norm(final Matrix m) {
		double sum = 0.0;
		for (int i = 0; i < m.rows(); ++i)
			for (int j = 0; j < m.columns(); ++j) {
				final double v = m.get(i, j);
				sum += v * v;
			}
		return Math.sqrt(sum);
	}
}
