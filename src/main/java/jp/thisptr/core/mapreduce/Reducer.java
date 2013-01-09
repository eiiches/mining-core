package jp.thisptr.core.mapreduce;

import jp.thisptr.core.tuple.Pair;

public interface Reducer<MappedKey, MappedValue, ReducedKey, ReducedValue> {
	Iterable<Pair<ReducedKey, ReducedValue>> reduce(final MappedKey key, final Iterable<MappedValue> value);
}
