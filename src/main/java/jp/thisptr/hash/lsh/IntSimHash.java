package jp.thisptr.hash.lsh;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

import java.util.Map;

import jp.thisptr.hash.IntMurmurHash;
import jp.thisptr.math.vector.SparseMapVector;
import jp.thisptr.math.vector.SparseVector;

public class IntSimHash {
	private static final int SIZE = Integer.SIZE;
	
	private static int finalize(final double[] v) {
		int result = 0;
		for (int j = 0; j < SIZE; ++j)
			if (v[j] > 0)
				result |= 1 << j;
		return result;
	}
	
	private static void update(final double[] v, final int basis, final double by) {
		for (int j = 0; j < SIZE; ++j) {
			if (((basis >> j) & 1) == 1) {
				v[j] += by;
			} else {
				v[j] -= by;
			}
		}
	}
	
	public int hash(final Map<String, Double> map) {
		final double[] v = new double[SIZE];
		for (Map.Entry<String, Double> value : map.entrySet())
			update(v, IntMurmurHash.hash(value.getKey()), value.getValue());
		return finalize(v);
	}
	
	public int hash(final SparseVector vector) {
		final double[] v = new double[SIZE];
		for (final Int2DoubleMap.Entry value : ((SparseMapVector) vector).rawMap().int2DoubleEntrySet())
			update(v, IntMurmurHash.hash(value.getIntKey()), value.getDoubleValue());
		return finalize(v);
	}
}