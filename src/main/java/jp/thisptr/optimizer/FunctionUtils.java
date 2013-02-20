package jp.thisptr.optimizer;

import org.apache.commons.lang.NotImplementedException;

import jp.thisptr.math.operation.ArrayOp;

public final class FunctionUtils {
	private FunctionUtils() { }
	
	public static Function negate(final Function f) {
		return new Function() {
			@Override
			public int xdim() {
				return f.xdim();
			}
			@Override
			public double f(final double[] x) {
				return -f.f(x);
			}
			@Override
			public double[] df(final double[] x) {
				final double[] df = f.df(x);
				ArrayOp.negate(df);
				return df;
			}
		};
	}
	
	public static L1RegularizedFunction l1Regularize(final Function f, final double regularizer) {
		throw new NotImplementedException();
	}
}
