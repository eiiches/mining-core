package net.thisptr.bandit.policy;

import java.util.List;

import net.thisptr.bandit.BanditArm;
import net.thisptr.util.SelectUtils;

public class EpsilonGreedy<T> extends AbstractBanditPolicy<T> {
	
	public static final double DEFAULT_EPSILON = 0.1;
	
	private final double epsilon;
	
	public EpsilonGreedy() {
		this(DEFAULT_EPSILON);
	}
	
	public EpsilonGreedy(final double epsilon) {
		this.epsilon = epsilon;
	}

	public double getEpsilon() {
		return epsilon;
	}

	@Override
	public List<BanditArm<T>> select(final int n, final int playCount, final List<BanditArm<T>> arms) {
		final double[] means = new double[arms.size()];
		for (int i = 0; i < arms.size(); ++i) {
			final BanditArm<T> arm = arms.get(i);
			means[i] = arm.getRewardCount() / (double) arm.getPullCount();
		}
		
		// Exploration: select randomly
		if (Math.random() < epsilon)
			return SelectUtils.toItems(arms, SelectUtils.random(arms.size(), n));
		
		// Exploitation: select the best arms
		return SelectUtils.toItems(arms, SelectUtils.best(means, n));
	}
}
