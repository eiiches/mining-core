package net.thisptr.math.vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

public class DenseByteBufferVector extends DenseVector {
	private static final int DOUBLE_BYTES = Double.SIZE / 8;

	private DoubleBuffer dbuf;
	private ByteBuffer buf;

	/**
	 * The new vector may not be initialized to zero.
	 * 
	 * @param size
	 */
	public DenseByteBufferVector(final int size) {
		this.buf = ByteBuffer.allocateDirect(size * DOUBLE_BYTES).order(ByteOrder.nativeOrder());
		this.dbuf = buf.asDoubleBuffer();
	}

	public DenseByteBufferVector(final double[] values) {
		this(values.length);
		for (int i = 0; i < values.length; ++i)
			set(i, values[i]);
	}

	public DenseByteBufferVector(final ByteBuffer buf) {
		this.buf = buf.duplicate().order(buf.order());
		this.dbuf = buf.asDoubleBuffer();
	}

	public DenseByteBufferVector(final Vector v) {
		buf = ByteBuffer.allocateDirect(v.size() * 8).order(ByteOrder.nativeOrder());
		dbuf = buf.asDoubleBuffer();
		v.walk(new Vector.VectorVisitor() {
			@Override
			public void visit(int index, double value) {
				dbuf.put(index, value);
			}
		});
	}

	@Override
	public int size() {
		return dbuf.remaining();
	}

	@Override
	public int capacity() {
		return dbuf.remaining();
	}

	@Override
	public void resize(final int size) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double get(final int index) {
		return dbuf.get(index);
	}

	@Override
	public void set(final int index, final double value) {
		dbuf.put(index, value);
	}

	@Override
	public void walk(final VectorVisitor visitor) {
		final int size = size();
		for (int i = 0; i < size; ++i) {
			final double v = dbuf.get(i);
			if (v != 0.0)
				visitor.visit(i, dbuf.get(i));
		}
	}

	public ByteBuffer raw() {
		return buf;
	}
}
