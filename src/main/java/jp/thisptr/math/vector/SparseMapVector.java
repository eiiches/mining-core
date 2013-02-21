package jp.thisptr.math.vector;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;

import java.util.Collections;
import java.util.Iterator;

public class SparseMapVector extends SparseVector {
	private final Int2DoubleMap map;
	
	public SparseMapVector() {
		map = new Int2DoubleOpenHashMap();
	}
	
	public SparseMapVector(final double... values) {
		this();
		for (int i = 0; i < values.length; ++i)
			set(i, values[i]);
	}

	@Override
	public double get(final int index) {
		return map.get(index);
	}

	@Override
	public void set(final int index, final double value) {
		if (value == 0.0) {
			map.remove(index);
			return;
		}
		map.put(index, value);
	}
	
	@Override
	public int size() {
		if (map.isEmpty())
			return 0;
		return Collections.max(map.keySet()) + 1;
	}
	
	@Override
	public int capacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void accept(final Visitor visitor) {
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
}
