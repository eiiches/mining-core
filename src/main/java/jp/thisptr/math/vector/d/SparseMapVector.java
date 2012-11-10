package jp.thisptr.math.vector.d;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;

import java.util.Map;

import jp.thisptr.core.util.CollectionUtils;

public class SparseMapVector extends SparseVector implements Vector.Modifiable, Vector.MapAccessible {
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
		Integer result = CollectionUtils.max(map.keySet());
		return result != null ? result + 1 : 0;
	}
	
	@Override
	public int capacity() {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public Int2DoubleMap rawMap() {
		return map;
	}
}
