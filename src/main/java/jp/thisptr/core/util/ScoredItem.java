package jp.thisptr.core.util;

import jp.thisptr.core.tuple.Pair;

public class ScoredItem<T, U> extends Pair<T, U> {
	public U getScore() { return getSecond(); }
	public T getItem() { return getFirst(); }
	public ScoredItem(final T item, final U score) {
		super(item, score);
	}
}