package jp.thisptr.core.util;

import jp.thisptr.core.tuple.Pair;

public class ScoredItem<ItemType, ScoreType> extends Pair<ItemType, ScoreType> {
	public ScoreType getScore() { return getSecond(); }
	public ItemType getItem() { return getFirst(); }
	public ScoredItem(final ItemType item, final ScoreType score) {
		super(item, score);
	}
}