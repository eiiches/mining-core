package net.thisptr.bandit.policy;

import java.util.List;

import net.thisptr.util.ScoredItem;

public abstract class AbstractBanditPolicy<T> implements BanditPolicy<T> {
	@Override
	public ScoredItem<T> play() {
		final List<ScoredItem<T>> selected = play(1);
		if (selected.isEmpty())
			return null;
		return selected.get(0);
	}
}
