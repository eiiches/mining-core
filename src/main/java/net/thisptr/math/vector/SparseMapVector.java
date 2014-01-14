package net.thisptr.math.vector;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public class SparseMapVector extends SparseVector {
	private int size;
	private final Int2DoubleMap map;

	public SparseMapVector(final int size) {
		this.size = size;
		this.map = new Int2DoubleOpenHashMap();
	}

	public SparseMapVector(final double[] values) {
		this(values.length);
		for (int i = 0; i < values.length; ++i)
			set(i, values[i]);
	}

	@Override
	public double get(final int index) {
		if (index >= size)
			throw new IndexOutOfBoundsException();

		return map.get(index);
	}

	@Override
	public void set(final int index, final double value) {
		if (index >= size)
			throw new IndexOutOfBoundsException();

		if (value == 0.0) {
			map.remove(index);
			return;
		}
		map.put(index, value);
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
	public void resize(final int size) {
		final ObjectIterator<Int2DoubleMap.Entry> iter = map.int2DoubleEntrySet().iterator();
		while (iter.hasNext()) {
			final Int2DoubleMap.Entry entry = iter.next();
			if (entry.getIntKey() >= size)
				iter.remove();
		}
		this.size = size;
	}

	@Override
	public double walk(final VectorVisitor visitor) {
		for (final Int2DoubleMap.Entry e : map.int2DoubleEntrySet())
			visitor.visit(e.getIntKey(), e.getDoubleValue());
		return visitor.finish();
	}

	public Int2DoubleMap raw() {
		return map;
	}
}
