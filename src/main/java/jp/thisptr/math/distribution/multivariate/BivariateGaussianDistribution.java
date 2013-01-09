package jp.thisptr.math.distribution.multivariate;

import org.apache.commons.lang.NotImplementedException;

public class BivariateGaussianDistribution implements MultivariateDistribution {
	private final double[] myu;
	private final double[][] sigma;
	private final double[][] sigmainv;
	
	public BivariateGaussianDistribution(final double[] myu, final double[][] sigma) {
		this.myu = myu;
		this.sigma = sigma;
		this.sigmainv = new double[][] {
				new double[] { sigma[0][0], sigma[0][1] },
				new double[] { sigma[1][0], sigma[1][1] }
		};
	}
	
	@Override
	public double at(final double[] x) {
		return Math.exp(a)
		throw new NotImplementedException();
		// TODO: implement
	}

	@Override
	public double[] sample() {
		throw new NotImplementedException();
	}
	
	
}
