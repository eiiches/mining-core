package net.thisptr.bandit.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thisptr.bandit.Bandit;
import net.thisptr.util.ScoredItem;
import net.thisptr.util.SelectUtils;

public class Softmax<T> extends AbstractBanditPolicy<T> {
	
	private final Bandit<T> bandit;
	private double temperature;
	private static final double DEFAULT_TEMPERATURE = 0.1;
	
	private static class ArmStat {
		private int nPulled = 0;
		private int nReward = 0;
	}
	
	private int nPlayed = 0;
	private final Map<T, ArmStat> armStats = new HashMap<T, ArmStat>();
	
	public Softmax(final Bandit<T> bandit) {
		this(bandit, DEFAULT_TEMPERATURE);
	}
	
	public Softmax(final Bandit<T> bandit, final double temperature) {
		this.bandit = bandit;
		this.temperature = temperature;
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
			
			scores[i] = Math.exp(stat.nReward / (double) stat.nPulled / temperature);
			means[i] = stat.nReward / (double) stat.nPulled;
		}
		nPlayed += 1;
		return pull(SelectUtils.toItems(bandit.getArms(), means, SelectUtils.roulette(scores, n)));
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
