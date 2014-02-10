package net.thisptr.math.vector;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.thisptr.math.matrix.Matrix;

public class SparseMapVector extends AbstractVector {
	private int size;
	private final Int2DoubleMap buf;
	private VectorShape shape;

	public SparseMapVector(final int size) {
		this(size, VectorShape.Column);
	}

	public SparseMapVector(final int size, final VectorShape shape) {
		this.size = size;
		this.buf = new Int2DoubleOpenHashMap();
		this.shape = shape;
	}

	public SparseMapVector(final int size, final VectorShape shape, final double[] initializer) {
		this(size, shape);
		final int isize = Math.min(size, initializer.length);
		for (int i = 0; i < isize; ++i)
			set(i, initializer[i]);
	}

	private SparseMapVector(final int size, final VectorShape shape, final Int2DoubleMap map) {
		this.size = size;
		this.shape = shape;
		this.buf = map;
	}

	@Override
	public double get(final int index) {
		if (index >= size)
			throw new IndexOutOfBoundsException();
		return buf.get(index);
	}

	@Override
	public void set(final int index, final double value) {
		if (index >= size)
			throw new IndexOutOfBoundsException();
		if (value == 0.0) {
			buf.remove(index);
			return;
		}
		buf.put(index, value);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int capacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	public VectorShape shape() {
		return shape;
	}

	@Override
	public void resize(final int size) {
		final ObjectIterator<Int2DoubleMap.Entry> iter = buf.int2DoubleEntrySet().iterator();
		while (iter.hasNext()) {
			final Int2DoubleMap.Entry entry = iter.next();
			if (entry.getIntKey() >= size)
				iter.remove();
		}
		this.size = size;
	}

	@Override
	public double walk(final VectorVisitor visitor) {
		for (final Int2DoubleMap.Entry e : buf.int2DoubleEntrySet())
			visitor.visit(e.getIntKey(), e.getDoubleValue());
		return visitor.finish();
	}

	public Int2DoubleMap raw() {
		return buf;
	}

	@Override
	public Vector column(int column) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Vector row(int row) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Matrix transpose() {
		return new SparseMapVector(size, shape.transpose(), buf);
	}
}
