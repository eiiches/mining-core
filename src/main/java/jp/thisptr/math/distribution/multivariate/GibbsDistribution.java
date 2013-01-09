package jp.thisptr.math.distribution.multivariate;

import jp.thisptr.math.structure.operation.ArrayOp;
import jp.thisptr.math.structure.vector.SparseMapVector;

import org.apache.commons.lang.NotImplementedException;

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
	public double at(final double[] x) {
		return Math.exp(ArrayOp.dot(x, lambda)) / z;
	}
	
	public double at(final SparseMapVector x) {
		throw new NotImplementedException();
	}
}
