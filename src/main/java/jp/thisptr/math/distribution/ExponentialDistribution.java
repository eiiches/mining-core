package jp.thisptr.math.distribution;

import jp.thisptr.lang.NotImplementedException;

/**
 * An implementation of exponential distribution: p(x) = λ exp(-λx), where λ > 0.
 * 
 * @author eiichi
 */
public class ExponentialDistribution implements Distribution {
	private final UniformDistribution uniform = new UniformDistribution(0.0, 1.0);
	
	/**
	 * A value of λ.
	 */
	private double lambda;
	
	/**
	 * @see #lambda
	 */
	public double getLambda() {
		return lambda;
	}

	/**
	 * @see #lambda
	 */
	public void setLambda(final double lambda) {
		this.lambda = lambda;
	}
	
	/**
	 * @param lambda {@link #lambda}
	 */
	public ExponentialDistribution(final double lambda) {
		this.lambda = lambda;
	}

	/**
	 * Draws a sample from the distribution using inverse transformation method.
	 */
	@Override
	public double sample() {
		return -Math.log(1 - uniform.sample()) / lambda;
	}

	@Override
	public double densityAt(final double x) {
		if (x < 0)
			return 0;
		return lambda * Math.exp(-lambda * x);
	}

	@Override
	public double cumulativeAt(final double x) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public double inverseCumulativeAt(final double p) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
}
