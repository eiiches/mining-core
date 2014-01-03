package net.thisptr.math.matrix;

import static org.junit.Assert.assertEquals;
import net.thisptr.math.vector.Vector;

import org.junit.Test;

public class DenseByteBufferMatrixTest {
	private static final double eps = 0.00001;

	private static double[] V(final double... v) {
		return v;
	}

	private static double[][] M(final double[]... r) {
		return r;
	}

	@Test
	public void testTranspose() {
		final Matrix m = new DenseByteBufferMatrix(1, 2);
		m.set(0, 0, 0);
		m.set(0, 1, 1);

		assertEquals(m.columns(), 2);
		assertEquals(m.rows(), 1);
		assertEquals(0, m.get(0, 0), eps);
		assertEquals(1, m.get(0, 1), eps);

		final Matrix t = m.transpose();
		assertEquals(t.columns(), 1);
		assertEquals(t.rows(), 2);
		assertEquals(0, t.get(0, 0), eps);
		assertEquals(1, t.get(1, 0), eps);

		final Matrix m_ = t.transpose(); // transposed back
		assertEquals(m_.columns(), 2);
		assertEquals(m_.rows(), 1);
		assertEquals(0, m_.get(0, 0), eps);
		assertEquals(1, m_.get(0, 1), eps);
	}

	@Test
	public void test_Row() {
		final Matrix m = new DenseByteBufferMatrix(M(V(2, 3), V(5, 7)));

		final Vector r0 = m.row(0);
		assertEquals(2, r0.size());
		assertEquals(2, r0.get(0), eps);
		assertEquals(3, r0.get(1), eps);

		final Vector r1 = m.row(1);
		assertEquals(2, r1.size());
		assertEquals(5, r1.get(0), eps);
		assertEquals(7, r1.get(1), eps);
	}

	@Test
	public void test_TransposeAndColumn() {
		final Matrix m = new DenseByteBufferMatrix(M(V(2, 3), V(5, 7)));

		final Vector c0 = m.transpose().column(0);
		assertEquals(2, c0.size());
		assertEquals(2, c0.get(0), eps);
		assertEquals(3, c0.get(1), eps);

		final Vector c1 = m.transpose().column(1);
		assertEquals(2, c1.size());
		assertEquals(5, c1.get(0), eps);
		assertEquals(7, c1.get(1), eps);
	}
}
