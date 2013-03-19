package net.thisptr.math.distribution;

import net.thisptr.math.SpecialFunctions;

/**
 * A gaussian distribution, p(x) = exp(-(x-μ)<sup>2</sup> / 2σ<sup>2</sup>) / sqrt(2πσ<sup>2</sup>).
 * 
 * @author eiichi
 */
public class GaussianDistribution implements Distribution {
	private final UniformDistribution uniform = new UniformDistribution(0.0, 1.0);
	
	/**
	 * A value of μ, an average of the gaussian.
	 */
	private double mu;
	
	/**
	 * @see #mu
	 */
	public double getMu() {
		return mu;
	}

	/**
	 * @see #mu
	 */
	public void setMu(final double mu) {
		this.mu = mu;
	}
	
	/**
	 * A value of σ, a deviation of the gaussian.
	 */
	private double sigma;

	/**
	 * @see #sigma
	 */
	public double getSigma() {
		return sigma;
	}

	/**
	 * @see #sigma
	 */
	public void setSigma(final double sigma) {
		this.sigma = sigma;
	}
	
	public GaussianDistribution() {
		this(0.0, 1.0);
	}
	
	public GaussianDistribution(final double mu, final double sigma) {
		this.mu = mu;
		this.sigma = sigma;
	}
	
	public double standardSample() {
		// Run Box-Muller's method, using a trick described in Numerical Recipes to
		// eliminate the use of rather slow trigonometric functions.
		double v1, v2, r2;
		do {
			v1 = 2.0 * uniform.sample() - 1.0;
			v2 = 2.0 * uniform.sample() - 1.0;
			r2 = v1 * v1 + v2 * v2;
		} while (r2 >= 1.0 || r2 == 0.0);
		return v1 * Math.sqrt(-2.0 * Math.log(r2) / r2);
	}

	/**
	 * Draws a sample from the distribution.
	 */
	@Override
	public double sample() {
		return (standardSample() + mu) * sigma;
	}
	
	public static double standardDensityAt(final double x) {
		return Math.exp(-x * x / 2.0) / Math.sqrt(2 * Math.PI);
	}
	
	@Override
	public double densityAt(final double x) {
		return standardDensityAt((x - mu) / sigma) / sigma;
	}
	
	public static double standardCumulativeAt(final double x) {
		return SpecialFunctions.gaussianCumulative(x);
	}
	
	@Override
	public double cumulativeAt(final double x) {
		return standardCumulativeAt((x - mu) / sigma);
	}
	
	public static double standardInverseCumulativeAt(final double p) {
		return SpecialFunctions.gaussianInverseCumulative(p);
	}
	
	@Override
	public double inverseCumulativeAt(final double p) {
		return mu + sigma * standardInverseCumulativeAt(p);
	}
}