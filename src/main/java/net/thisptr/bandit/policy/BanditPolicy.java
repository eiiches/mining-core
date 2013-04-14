package net.thisptr.bandit.policy;

import java.util.List;

import net.thisptr.util.ScoredItem;

public interface BanditPolicy<T> {
	ScoredItem<T> play();
	List<ScoredItem<T>> play(final int n);
	void reward(final T arm);
}
