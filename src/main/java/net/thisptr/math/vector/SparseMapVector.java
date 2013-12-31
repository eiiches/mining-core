package net.thisptr.math.vector;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.util.Iterator;

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
	public void walk(final Visitor visitor) {
		for (final Int2DoubleMap.Entry e : map.int2DoubleEntrySet())
			visitor.visit(e.getIntKey(), e.getDoubleValue());
	}

	@Override
	public Iterator<Element> iterator() {
		final Iterator<Int2DoubleMap.Entry> iter = map.int2DoubleEntrySet().iterator();
		return new Iterator<Vector.Element>() {
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public Element next() {
				final Int2DoubleMap.Entry e = iter.next();
				return new Element() {
					@Override
					public double value() {
						return e.getDoubleValue();
					}

					@Override
					public int index() {
						return e.getIntKey();
					}
				};
			}

			@Override
			public void remove() {
				iter.remove();
			}
		};
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("[");
		String sep = "";
		for (final Element element : this) {
			builder.append(sep);
			builder.append(String.format("%d: %.2f", element.index(), element.value()));
			sep = ", ";
		}
		builder.append("]");
		return builder.toString();
	}

	public Int2DoubleMap raw() {
		return map;
	}
}
