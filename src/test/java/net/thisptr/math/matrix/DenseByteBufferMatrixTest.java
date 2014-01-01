package net.thisptr.math.matrix;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DenseByteBufferMatrixTest {
	private static final double eps = 0.00001;

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
}
