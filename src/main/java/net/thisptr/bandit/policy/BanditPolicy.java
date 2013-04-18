package net.thisptr.bandit.policy;

import java.util.List;

import net.thisptr.bandit.BanditArm;

public interface BanditPolicy<T> {
	BanditArm<T> select(final int playCount, final List<BanditArm<T>> arms);
	List<BanditArm<T>> select(final int n, final int playCount, final List<BanditArm<T>> arms);
}
