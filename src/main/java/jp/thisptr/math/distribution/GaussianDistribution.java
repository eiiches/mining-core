package jp.thisptr.math.distribution;

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

	/**
	 * Draws a sample from the distribution using Box-Muller's method.
	 */
	@Override
	public double sample() {
		final double x = uniform.sample();
		final double y = uniform.sample();
		final double r = Math.sqrt(-2 * Math.log(x));
		final double theta = 2 * Math.PI * y;
		final double n = r * Math.cos(theta);
		return (n + mu) * sigma;
	}
	
	@Override
	public double densityAt(final double x) {
		return Math.exp(-Math.pow(x - mu, 2) / (2 * sigma * sigma)) / Math.sqrt(2 * Math.PI * sigma * sigma);
	}
}