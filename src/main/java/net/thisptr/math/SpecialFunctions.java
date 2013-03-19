package net.thisptr.math;


public final class SpecialFunctions {
	private SpecialFunctions() { }
	
	private static double gammaLnByLanczosApproximation(final double x) {
		if (x <= 0)
			return Double.NaN;
		
		/* parameters */
		final int gamma = 5;
		final double c0 = 1.000000000190015;
		final double[] c = new double[] {76.18009172947146, -86.50532032941677,
				24.01409824083091, -1.231739572450155,
				0.1208650973866179e-2, -0.5395239384953e-5};
	
		/* computation */
		final double t = x + gamma + 0.5;
		double sum = c0;
		for (int i = 0; i < c.length; ++i)
			sum += c[i] / (x + i + 1);
		return Math.log(t) * (x + 0.5) - t + Math.log(Math.sqrt(2 * Math.PI) * sum / x);
	}
	
	/**
	 * Returns the log of gamma function at <tt>x</tt>.
	 * @param x > 0
	 * @return
	 */
	public static double gammaLn(final double x) {
		return gammaLnByLanczosApproximation(x);
	}
	
	/**
	 * Computes the regularized upper gamma function. This converges rapidly for x greater than about a + 1.
	 * @param a
	 * @param x
	 * @return
	 */
	private static double regularizedUpperGammaByContinuedFraction(final double a, final double x) {
		final double relativeError = 1e-10;
		
		// setup
		double D = 1.0 / (x + 1.0 - a);
		double dh = D;
		double h = dh;
		
		// loop to converge
		for (int i = 1;; ++i) {
			final double bn = x + 2.0 * i + 1.0 - a;
			final double an = i * (a - i);
			
			D = 1.0 / (bn + an * D);
			dh *= bn * D - 1.0;
			h += dh;
			
			if (Math.abs(dh) < Math.abs(h) * relativeError)
				return Math.exp(-x + a * Math.log(x) - gammaLn(a)) * h;
		}
	}
	
	/**
	 * Computes the regularized lower gamma function. This converges rapidly for x less than about a + 1.
	 * @param a
	 * @param x
	 * @return
	 */
	private static double regularizedLowerGammaByPowerSeries(final double a, final double x) {
		final double relativeError = 1e-10;
		
		double term = 1.0 / a;
		double sum = 0.0;
		for (int i = 1;; ++i) {
			sum += term;
			term *= x / (a + i);
			if (Math.abs(term) < Math.abs(sum) * relativeError)
				return sum * Math.exp(-x + a * Math.log(x) - gammaLn(a));
		}
	}
	
	/**
	 * Returns the value of incomplete gamma function <tt>P(a, x) = γ(a, x) / Γ(a)</tt>,
	 * where <tt>γ(a, x) = ∫<sub>0</sub><sup>x</sup> e<sup>-t</sup> t <sup>a-1</sup> dt</tt>.
	 * @param a > 0
	 * @param x ≧ 0
	 * @return
	 */
	public static double regularizedLowerGamma(final double a, final double x) {
		if (x < 0.0 || a <= 0.0)
			return Double.NaN;

		if (x < a + 1.0)
			return regularizedLowerGammaByPowerSeries(a, x);
		return 1.0 - regularizedUpperGammaByContinuedFraction(a, x);
	}
	
	public static double regularizedUpperGamma(final double a, final double x) {
		if (x < 0.0 || a <= 0.0)
			return Double.NaN;
		
		if (x < a + 1.0)
			return 1.0 - regularizedLowerGammaByPowerSeries(a, x);
		return regularizedUpperGammaByContinuedFraction(a, x);
	}
	
	/**
	 * Computes a value of cumulative distribution function of standard Gaussian at x.
	 * @param x
	 * @return
	 */
	public static double gaussianCumulative(final double x) {
		return (erf(x / Math.sqrt(2)) + 1) / 2;
	}
	
	/**
	 * Computes the inverse normal cumulative distribution.
	 * <p>Reference: Michael J. Wichura, Applied Statistics, vol. 37, 1988, pp. 477-484.</p>
	 * @param p
	 * @return
	 */
	public static double gaussianInverseCumulative(final double p) {
		if (p <= 0.0 || 1.0 <= p) {
			if (p == 0.0)
				return Double.NEGATIVE_INFINITY;
			if (p == 1.0)
				return Double.POSITIVE_INFINITY;
			return Double.NaN;
		}
		
		double result;
		
		double q = p - 0.5;
		if (p < 0.5)
			q = -q;
		
		if (q <= 0.425) {
			final double[] a = new double[] {
					3.3871328727963666080, 1.3314166789178437745e+2, 1.9715909503065514427e+3, 1.3731693765509461125e+4,
					4.5921953931549871457e+4, 6.7265770927008700853e+4, 3.3430575583588128105e+4, 2.5090809287301226727e+3,
			};
			final double[] b = new double[] {
					1.0, 4.2313330701600911252e+1, 6.8718700749205790830e+2, 5.3941960214247511077e+3,
					2.1213794301586595867e+4, 3.9307895800092710610e+4, 2.8729085735721942674e+4, 5.2264952788528545610e+3,
			};
			final double r = 0.180625 - q * q;
			result = q * (((((((a[7] * r + a[6]) * r + a[5]) * r + a[4]) * r + a[3]) * r + a[2]) * r + a[1]) * r + a[0]) /
					(((((((b[7] * r + b[6]) * r + b[5]) * r + b[4]) * r + b[3]) * r + b[2]) * r + b[1]) * r + b[0]);
		} else if (q <= 0.49999999998611205) {
			final double[] a = new double[] {
					1.42343711074968357734, 4.63033784615654529590, 5.76949722146069140550, 3.64784832476320460504,
					1.27045825245236838258, 2.41780725177450611770e-1, 2.27238449892691845833e-2, 7.74545014278341407640e-4,
			};
			final double[] b = new double[] {
					1.0, 2.05319162663775882187, 1.67638483018380384940, 6.89767334985100004550e-1,
					1.48103976427480074590e-1, 1.51986665636164571966e-2, 5.47593808499534494600e-4, 1.05075007164441684324e-9,
			};
			final double r = Math.sqrt(-Math.log(0.5 - q)) - 1.6;
			result = (((((((a[7] * r + a[6]) * r + a[5]) * r + a[4]) * r + a[3]) * r + a[2]) * r + a[1]) * r + a[0]) /
					(((((((b[7] * r + b[6]) * r + b[5]) * r + b[4]) * r + b[3]) * r + b[2]) * r + b[1]) * r + b[0]);
		} else {
			final double[] a = new double[] {
					6.65790464350110377720, 5.46378491116411436990, 1.78482653991729133580, 2.96560571828504891230e-1,
					2.65321895265761230930e-2, 1.24266094738807843860e-3, 2.71155556874348757815e-5, 2.01033439929228813265e-7,
			};
			final double[] b = new double[] {
					1.0, 5.99832206555887937690e-1, 1.36929880922735805310e-1, 1.48753612908506148525e-2,
					7.86869131145613259100e-4, 1.84631831751005468180e-5, 1.42151175831644588870e-7, 2.04426310338993978564e-15,
			};
			final double r = Math.sqrt(-Math.log(0.5 - q)) - 5.0;
			result = (((((((a[7] * r + a[6]) * r + a[5]) * r + a[4]) * r + a[3]) * r + a[2]) * r + a[1]) * r + a[0]) /
					(((((((b[7] * r + b[6]) * r + b[5]) * r + b[4]) * r + b[3]) * r + b[2]) * r + b[1]) * r + b[0]);
		}
		
		if (p < 0.5)
			result = -result;
		
		return result;
	}
	
	/**
	 * Returns the value of inverse error function, so that <tt>x = invErf({@link #erf(double) erf(x)})</tt>.
	 * @param x
	 * @return
	 */
	public static double invErf(final double x) {
		return gaussianInverseCumulative((x + 1) / 2.0) / Math.sqrt(2);
	}
	
	/**
	 * Returns the value of error function at <tt>x</tt>.
	 * @param x
	 * @return
	 */
	public static double erf(final double x) {
		if (x < 0.0)
			return -regularizedLowerGamma(0.5, x * x);
		return regularizedLowerGamma(0.5, x * x);
	}
	
	/**
	 * Returns the value of complementary error function at <tt>x</tt>.
	 * @param x
	 * @return
	 */
	public static double erfc(final double x) {
		return 1.0 - erf(x);
	}
}
