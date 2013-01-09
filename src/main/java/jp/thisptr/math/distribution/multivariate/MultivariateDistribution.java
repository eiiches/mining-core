package jp.thisptr.math.distribution.multivariate;

public interface MultivariateDistribution {
	double[] sample();
	double at(final double[] x);
}
