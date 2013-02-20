package jp.thisptr.math.distribution.multivariate;

public interface MultivariateDistribution {
	double[] sample();
	double densityAt(final double[] x);
}
