package net.thisptr.math.vector;

import java.nio.ByteBuffer;

import net.thisptr.math.matrix.DenseByteBufferMatrix;

public class DenseByteBufferVector extends DenseByteBufferMatrix implements Vector {
	private VectorShape shape;
	private int size;

	public DenseByteBufferVector(final int size) {
		this(size, VectorShape.Column);
	}

	public DenseByteBufferVector(final int size, final VectorShape shape) {
		super(shape == VectorShape.Column ? size : 1, shape == VectorShape.Row ? size : 1);
		this.shape = shape;
		this.size = size;
		for (int i = 0; i < size; ++i)
			set(i, 0.0);
	}

	private DenseByteBufferVector(final int size, final VectorShape shape, final ByteBuffer buf) {
		super(buf, shape == VectorShape.Column ? size : 1, shape == VectorShape.Row ? size : 1);
		this.shape = shape;
		this.size = size;
	}

	public DenseByteBufferVector(final int size, final VectorShape shape, final double[] initializer) {
		this(size, shape);

		final int isize = Math.min(initializer.length, size);
		for (int i = 0; i < isize; ++i)
			set(i, initializer[i]);
	}

	public DenseByteBufferVector(final Vector v) {
		this(v.size(), v.shape());
		v.walk(new VectorVisitor() {
			@Override
			public void visit(int index, double value) {
				DenseByteBufferVector.this.set(index, value);
			}
		});
	}

	@Override
	public VectorShape shape() {
		return shape;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int capacity() {
		return size();
	}

	@Override
	public void resize(final int size) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resize(int rows, int columns) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Vector transpose() {
		return new DenseByteBufferVector(size, shape.transpose(), buf);
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
	public double walk(final VectorVisitor visitor) {
		final int size = size();
		for (int i = 0; i < size; ++i) {
			final double v = dbuf.get(i);
			if (v != 0.0)
				visitor.visit(i, dbuf.get(i));
		}
		return visitor.finish();
	}

	public ByteBuffer raw() {
		return buf;
	}

	public static DenseByteBufferVector wrap(final int size, final VectorShape shape, final ByteBuffer buf) {
		return new DenseByteBufferVector(size, shape, buf);
	}
}
