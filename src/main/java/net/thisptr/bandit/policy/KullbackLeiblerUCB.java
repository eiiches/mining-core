package net.thisptr.bandit.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thisptr.bandit.Bandit;
import net.thisptr.util.ScoredItem;
import net.thisptr.util.SelectUtils;

public class KullbackLeiblerUCB<T> extends AbstractBanditPolicy<T> {
	
	private final Bandit<T> bandit;
	private final double c;
	
	public static final double DEFAULT_C = 0.0;
	private static final int N_ITERATION = 10;
	
	private static class ArmStat {
		private int nPulled = 0;
		private int nReward = 0;
	}
	
	private int nPlayed = 0;
	private final Map<T, ArmStat> armStats = new HashMap<T, ArmStat>();
	
	public KullbackLeiblerUCB(final Bandit<T> bandit) {
		this(bandit, DEFAULT_C);
	}
	
	public KullbackLeiblerUCB(final Bandit<T> bandit, final double c) {
		this.bandit = bandit;
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
	public List<ScoredItem<T>> play(final int n) {
		final double[] scores = new double[bandit.size()];
		final double[] means = new double[bandit.size()];
		for (int i = 0; i < bandit.size(); ++i) {
			final T arm = bandit.getArms().get(i);
			
			ArmStat stat = armStats.get(arm);
			if (stat == null) {
				stat = new ArmStat();
				stat.nPulled = 1;
				stat.nReward = 1;
				nPlayed += 1;
				armStats.put(arm, stat);
			}
			
			scores[i] = computeScore(stat.nReward, stat.nPulled, nPlayed, c);
			means[i] = stat.nReward / (double) stat.nPulled;
		}
		nPlayed += 1;
		return pull(SelectUtils.toItems(bandit.getArms(), means, SelectUtils.best(scores, n)));
	}
	
	private List<ScoredItem<T>> pull(final List<ScoredItem<T>> arms) {
		for (final ScoredItem<T> arm : arms) {
			final ArmStat stat = armStats.get(arm.item());
			stat.nPulled += 1;
		}
		return arms;
	}
	
	@Override
	public void reward(final T arm) {
		final ArmStat stat = armStats.get(arm);
		stat.nReward += 1;
	}
	
}
