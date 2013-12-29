package net.thisptr.neuralnet;

/**
 * This class is thread safe.
 */
public class FastLogisticFunction {
	public static final double[] table = new double[10000];
	static {
		for (int i = 0; i < 10000; ++i) {
			final double x = ((i - 50 * 100) / 100.0);
			table[i] = _logistic(x);
		}
	}

	public static double logistic(double x) {
		final int i = (int)((x + 50.0) * 100.0);
		if (i < 0)
			return 0.0;
		if (10000 <= i)
			return 1.0;
		return table[i];
	}

	private static double _logistic(final double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}
}