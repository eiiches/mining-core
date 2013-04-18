package net.thisptr.bandit.policy;

import java.util.List;

import net.thisptr.bandit.BanditArm;
import net.thisptr.util.SelectUtils;

public class KullbackLeiblerUCB<T> extends AbstractBanditPolicy<T> {
	
	private final double c;
	
	public static final double DEFAULT_C = 0.0;
	private static final int N_ITERATION = 10;
	
	public KullbackLeiblerUCB() {
		this(DEFAULT_C);
	}
	
	public KullbackLeiblerUCB(final double c) {
		this.c = c;
	}
	
	private static double bernoulliKL(final double p, final double q) {
		return p * Math.log(p / q) + (1 - p) * Math.log((1 - p) / (1 - q));
	}
	
	private static double computeScore(final double nReward, final double nPulled, final double nPlayed, final double c) {
		final double bound = (Math.log(nPlayed) + c * Math.log(Math.log(nPlayed))) / nPulled;
		
		double lo = nReward / nPulled;
		double hi = 1.0;
		double q = (lo + hi) / 2;
		
		for (int i = 0; i < N_ITERATION; ++i) {
			if (bernoulliKL(nReward / nPulled, q) < bound) {
				lo = q;
			} else {
				hi = q;
			}
			q = (lo + hi) / 2;
		}
		
		return q;
	}

	@Override
	public List<BanditArm<T>> select(final int n, final int playCount, final List<BanditArm<T>> arms) {
		final double[] scores = new double[arms.size()];
		for (int i = 0; i < arms.size(); ++i) {
			final BanditArm<T> arm = arms.get(i);
			scores[i] = computeScore(arm.getRewardCount(), arm.getPullCount(), playCount, c);
		}
		return SelectUtils.toItems(arms, SelectUtils.best(scores, n));
	}
}
