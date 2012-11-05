package jp.thisptr.math.vector.d;

import java.util.HashMap;
import java.util.Map;

import jp.thisptr.core.util.CollectionUtils;

public class SparseMapVector extends SparseVector {
	private final Map<Integer, Double> map;
	private transient IndexedValue[] _valuesCache = null;
	
	public SparseMapVector() {
		map = new HashMap<Integer, Double>();
	}

	@Override
	public double get(final int index) {
		Double value = map.get(index);
		if (value == null)
			return 0.0;
		return value;
	}

	@Override
	public void set(final int index, final double value) {
		_valuesCache = null;
		if (value == 0.0) {
			map.remove(index);
			return;
		}
		map.put(index, value);
	}

	/**
	 * The returned values must not be changed.
	 */
	@Override
	public IndexedValue[] values() {
		if (_valuesCache != null)
			return _valuesCache;
		
		final IndexedValue[] result = new IndexedValue[map.size()];
		
		int i = 0;
		for (final Map.Entry<Integer, Double> v : map.entrySet())
			result[i++] = new IndexedValue(v.getKey(), v.getValue());
		
		_valuesCache = result;
		return result;
	}
	
	@Override
	public int dim() {
		Integer result = CollectionUtils.max(map.keySet());
		return result != null ? result + 1 : 0;
	}
	
	@Override
	public int capacity() {
		return Integer.MAX_VALUE;
	}
}
