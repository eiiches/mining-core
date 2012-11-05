package jp.thisptr.math.distribution;

public abstract class Distribution {
	public abstract double sample();
	public double[] sample(final int n) {
		final double[] result = new double[n];
		for (int i = 0; i < n; ++i)
			result[i] = sample();
		return result;
	}
}
