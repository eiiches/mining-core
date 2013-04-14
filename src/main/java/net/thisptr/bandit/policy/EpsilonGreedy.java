package net.thisptr.bandit.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thisptr.bandit.Bandit;
import net.thisptr.util.ScoredItem;
import net.thisptr.util.SelectUtils;

public class EpsilonGreedy<T> extends AbstractBanditPolicy<T> {
	
	public static final double DEFAULT_EPSILON = 0.1;
	
	private final Bandit<T> bandit;
	private final double epsilon;
	
	private static class ArmStat {
		private int nPulled = 0;
		private int nReward = 0;
	}
	
	private final Map<T, ArmStat> armStats = new HashMap<T, ArmStat>();
	
	public EpsilonGreedy(final Bandit<T> bandit) {
		this(bandit, DEFAULT_EPSILON);
	}
	
	public EpsilonGreedy(final Bandit<T> bandit, final double epsilon) {
		this.bandit = bandit;
		this.epsilon = epsilon;
	}
	
	/**
	 * Play bandit. The returned score is a click rate.
	 * @see net.thisptr.bandit.policy.BanditPolicy#play(int)
	 */
	@Override
	public List<ScoredItem<T>> play(final int n) {
		final double[] means = new double[bandit.size()];
		for (int i = 0; i < bandit.size(); ++i) {
			final T arm = bandit.getArms().get(i);
			
			ArmStat stat = armStats.get(arm);
			if (stat == null) {
				stat = new ArmStat();
				stat.nPulled = 1;
				stat.nReward = 1;
				armStats.put(arm, stat);
			}
			
			means[i] = stat.nReward / (double) stat.nPulled;
		}
		
		// Exploration: select randomly
		if (Math.random() < epsilon)
			return pull(SelectUtils.toItems(bandit.getArms(), means, SelectUtils.random(bandit.size(), n)));
		
		// Exploitation: select the best arms
		return pull(SelectUtils.toItems(bandit.getArms(), means, SelectUtils.best(means, n)));
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

	public double getEpsilon() {
		return epsilon;
	}
}
