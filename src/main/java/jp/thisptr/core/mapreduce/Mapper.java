package jp.thisptr.core.mapreduce;

import jp.thisptr.core.tuple.Pair;

public interface Mapper<InputKey, InputValue, MappedKey, MappedValue> {
	Iterable<Pair<MappedKey, MappedValue>> map(final InputKey key, final InputValue value);
}
