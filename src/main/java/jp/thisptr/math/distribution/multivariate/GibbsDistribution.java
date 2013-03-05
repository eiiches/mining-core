package jp.thisptr.math.distribution.multivariate;

import jp.thisptr.lang.NotImplementedException;
import jp.thisptr.math.operation.ArrayOp;
import jp.thisptr.math.vector.SparseMapVector;

public class GibbsDistribution implements MultivariateDistribution {
	private final double z;
	private final double[] lambda;
	
	public GibbsDistribution(final double z, final double[] lambda) {
		this.z = z;
		this.lambda = lambda.clone();
	}

	@Override
	public double[] sample() {
		throw new NotImplementedException();
	}

	@Override
	public double densityAt(final double[] x) {
		return Math.exp(ArrayOp.dot(x, lambda)) / z;
	}
	
	public double densityAt(final SparseMapVector x) {
		throw new NotImplementedException();
	}
}
