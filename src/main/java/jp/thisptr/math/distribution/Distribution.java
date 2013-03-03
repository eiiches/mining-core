package jp.thisptr.math.distribution;

public interface Distribution {
	
	/**
	 * Draws a sample from the distribution.
	 * @return
	 */
	double sample();
	
	/**
	 * Computes a value of probability density function at <tt>x</tt>.
	 * @param x
	 * @return
	 */
	double densityAt(final double x);
	
	/**
	 * Computes a value of cumulative distribution function at <tt>x</tt>, which is
	 * equivalent to integrating {@link #densityAt(double)} from <tt>-∞</tt> to <tt>x</tt>.
	 * @param x
	 * @return
	 */
	double cumulativeAt(final double x);
	
	/**
	 * Inverse function of {@link #cumulativeAt(double)}.
	 * @param p
	 * @return
	 */
	double inverseCumulativeAt(final double p);
}
