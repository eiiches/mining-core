package net.thisptr.bandit.policy;

import java.util.List;

import net.thisptr.bandit.BanditArm;
import net.thisptr.util.SelectUtils;

public class UCB1<T> extends AbstractBanditPolicy<T> {
	@Override
	public List<BanditArm<T>> select(final int n, final int playCount, final List<BanditArm<T>> arms) {
		final double[] scores = new double[arms.size()];
		for (int i = 0; i < arms.size(); ++i) {
			final BanditArm<T> arm = arms.get(i);
			scores[i] = arm.getRewardCount() / (double) arm.getPullCount()
					+ Math.sqrt(2 * Math.log(playCount) / arm.getPullCount());
		}
		return SelectUtils.toItems(arms, SelectUtils.best(scores, n));
	}
}
