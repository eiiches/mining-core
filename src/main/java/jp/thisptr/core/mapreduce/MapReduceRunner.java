package jp.thisptr.core.mapreduce;

import java.util.List;

import jp.thisptr.core.tuple.Pair;

import org.apache.commons.lang.NotImplementedException;

public class MapReduceRunner<InputKey, InputValue, ReducedKey, ReducedValue> {
	
	public <MappedKey, MappedValue> MapReduceRunner(final Mapper<InputKey, InputValue, MappedKey, MappedValue> mapper,
			final Reducer<MappedKey, MappedValue, ReducedKey, ReducedValue> reducer) {
		throw new NotImplementedException();
	}
	
	public List<Pair<ReducedKey, ReducedValue>> mapreduce(final Iterable<Pair<InputKey, InputValue>> input) {
		throw new NotImplementedException();
	}
}