package net.thisptr.bandit.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thisptr.bandit.Bandit;
import net.thisptr.util.ScoredItem;
import net.thisptr.util.SelectUtils;

public class UCB1<T> extends AbstractBanditPolicy<T> {
	
	private final Bandit<T> bandit;
	
	private static class ArmStat {
		private int nPulled = 0;
		private int nReward = 0;
	}
	
	private int nPlayed = 0;
	private final Map<T, ArmStat> armStats = new HashMap<T, ArmStat>();
	
	public UCB1(final Bandit<T> bandit) {
		this.bandit = bandit;
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
			
			scores[i] = stat.nReward / (double) stat.nPulled
					+ Math.sqrt(2 * Math.log(nPlayed) / stat.nPulled);
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
