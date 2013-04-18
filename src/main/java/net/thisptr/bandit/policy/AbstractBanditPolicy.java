package net.thisptr.bandit.policy;

import java.util.List;

import net.thisptr.bandit.BanditArm;

public abstract class AbstractBanditPolicy<T> implements BanditPolicy<T> {
	public BanditArm<T> select(final int playCount, final List<BanditArm<T>> arms) {
		final List<BanditArm<T>> selected = select(1, playCount, arms);
		if (selected.isEmpty())
			return null;
		return selected.get(0);
	}
}
