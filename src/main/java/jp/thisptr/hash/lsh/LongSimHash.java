package jp.thisptr.hash.lsh;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

import java.util.Map;

import jp.thisptr.hash.LongMurmurHash;
import jp.thisptr.math.structure.vector.SparseMapVector;
import jp.thisptr.math.structure.vector.SparseVector;

public class LongSimHash {
	private static final int SIZE = Long.SIZE;
	
	private static long finalize(final double[] v) {
		long result = 0;
		for (int j = 0; j < SIZE; ++j)
			if (v[j] > 0)
				result |= 1 << j;
		return result;
	}
	
	private static void update(final double[] v, final long basis, final double by) {
		for (int j = 0; j < SIZE; ++j) {
			if (((basis >> j) & 1) == 1) {
				v[j] += by;
			} else {
				v[j] -= by;
			}
		}
	}
	
	public long hash(final Map<String, Double> map) {
		final double[] v = new double[SIZE];
		for (Map.Entry<String, Double> value : map.entrySet())
			update(v, LongMurmurHash.hash(value.getKey()), value.getValue());
		return finalize(v);
	}
	
	public long hash(final SparseVector vector) {
		final double[] v = new double[SIZE];
		for (final Int2DoubleMap.Entry value : ((SparseMapVector) vector).rawMap().int2DoubleEntrySet())
			update(v, LongMurmurHash.hash(value.getIntKey()), value.getDoubleValue());
		return finalize(v);
	}
}