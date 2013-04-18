package net.thisptr.bandit.policy;

import java.util.List;

import net.thisptr.bandit.BanditArm;
import net.thisptr.util.SelectUtils;

public class Softmax<T> extends AbstractBanditPolicy<T> {
	
	private double temperature;
	private static final double DEFAULT_TEMPERATURE = 0.1;
	
	public Softmax() {
		this(DEFAULT_TEMPERATURE);
	}
	
	public Softmax(final double temperature) {
		this.temperature = temperature;
	}

	@Override
	public List<BanditArm<T>> select(final int n, final int playCount, final List<BanditArm<T>> arms) {
		final double[] scores = new double[arms.size()];
		for (int i = 0; i < arms.size(); ++i) {
			final BanditArm<T> arm = arms.get(i);
			scores[i] = Math.exp(arm.getRewardCount() / (double) arm.getPullCount() / temperature);
		}
		return SelectUtils.toItems(arms, SelectUtils.roulette(scores, n));
	}
}
