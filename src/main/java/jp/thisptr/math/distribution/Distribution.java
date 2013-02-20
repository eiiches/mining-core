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
}
