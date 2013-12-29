package net.thisptr.neuralnet;

import net.thisptr.math.distribution.Distribution;

/**
 * This class is not thread safe. Use with ThreadLocal.
 */
public class CachedSampler {
	private final int period;
	private double[] table;
	private int i;

	public CachedSampler(final int period, final Distribution distribution) {
		this.period = period;
		this.table = new double[period];
		
		for (int i = 0; i < period; ++i)
			this.table[i] = distribution.sample();
	}
	
	public double next() {
		if (i >= period)
			i = 0;
		return table[i++];
	}
}