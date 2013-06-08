package net.thisptr.graph.algorithm;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.thisptr.graph.IncidenceGraph;
import net.thisptr.util.ProgressLogging;
import net.thisptr.util.ScoredItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomWalkWithRestart {
	private static Logger log = LoggerFactory.getLogger(RandomWalkWithRestart.class);
	
	private final Random random = new Random();
	
	private final IncidenceGraph g;
	private final int[] seeds;
	
	public static final double DEFAULT_RESTART_PROBABILITY = 0.1;
	private double restartProbability = DEFAULT_RESTART_PROBABILITY;
	
	public static final double DEFAULT_STAY_PROBABILITY = 0.0;
	private double stayProbability = DEFAULT_STAY_PROBABILITY;
	
	public static final int DEFAULT_MAX_WALK_STEPS = -1;
	private int maxWalkSteps = DEFAULT_MAX_WALK_STEPS;
	
	final Int2DoubleMap visitCount = new Int2DoubleOpenHashMap();
	{
		visitCount.defaultReturnValue(0.0);
	}
	
	public RandomWalkWithRestart(final IncidenceGraph g, final int seed) {
		this(g, new int[] { seed });
	}
	
	public RandomWalkWithRestart(final IncidenceGraph g, final int[] seeds) {
		this.g = g;
		this.seeds = Arrays.copyOf(seeds, seeds.length);
	}
	
	public RandomWalkWithRestart walk() {
		int current = doSelectInitial(seeds);
		for (int walkStep = 0; getMaxWalkSteps() < 0 || walkStep < getMaxWalkSteps(); ++walkStep) {
			visitCount.put(current, visitCount.get(current) + 1);
			current = selectNext(current);
			if (current < 0)
				break;
		}
		return this;
	}
	
	public RandomWalkWithRestart walk(final int n) {
		final ProgressLogging plog = new ProgressLogging(log, "", n, 40, 1000000);
		for (int i = 0; i < n; ++i) {
			walk();
			plog.log(i);
		}
		plog.complete();
		return this;
	}
	
	protected int doSelectInitial(final int[] seeds) {
		return seeds[random.nextInt(seeds.length)];
	}
	
	protected int doSelectNext(final int source) {
		final int[] targets = g.getOutEdges(source);
		return targets[random.nextInt(targets.length)];
	}
	
	private int selectNext(final int source) {
		double r = random.nextDouble();
		
		if (r < restartProbability)
			return -1;
		r = r / (r - restartProbability); // rescale
		
		if (r < getStayProbability())
			return source;
		
		return doSelectNext(source);
	}
	
	public List<ScoredItem<Integer>> complete() {
		return complete(false);
	}
	
	public List<ScoredItem<Integer>> complete(final boolean toDistribution) {
		final List<ScoredItem<Integer>> results = new ArrayList<ScoredItem<Integer>>(visitCount.size());
		
		if (toDistribution) {
			double sum = 0.0;
			for (Int2DoubleMap.Entry entry : visitCount.int2DoubleEntrySet())
				sum += entry.getDoubleValue();
			for (Int2DoubleMap.Entry entry : visitCount.int2DoubleEntrySet())
				results.add(new ScoredItem<Integer>(entry.getKey(), entry.getDoubleValue() / sum));
		} else {
			for (Int2DoubleMap.Entry entry : visitCount.int2DoubleEntrySet())
				results.add(new ScoredItem<Integer>(entry.getKey(), entry.getDoubleValue()));
		}
		
		Collections.sort(results);
		return results;
	}

	public double getRestartProbability() {
		return restartProbability;
	}

	public RandomWalkWithRestart setRestartProbability(double restartProbability) {
		this.restartProbability = restartProbability;
		return this;
	}

	public int getMaxWalkSteps() {
		return maxWalkSteps;
	}

	public RandomWalkWithRestart setMaxWalkSteps(int maxWalkSteps) {
		this.maxWalkSteps = maxWalkSteps;
		return this;
	}

	public double getStayProbability() {
		return stayProbability;
	}

	public RandomWalkWithRestart setStayProbability(double stayProbability) {
		this.stayProbability = stayProbability;
		return this;
	}
}