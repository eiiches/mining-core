package jp.thisptr.math.optimizer;

import jp.thisptr.math.vector.d.ArrayVector;
import jp.thisptr.math.vector.d.DenseArrayVector;
import jp.thisptr.math.vector.d.Vector;

public final class FunctionUtils {
	private FunctionUtils() { }
	
	public static Function negate(final Function f) {
		return new Function() {
			@Override
			public int xdim() {
				return f.xdim();
			}
			@Override
			public double f(final Vector x) {
				return -f.f(x);
			}
			@Override
			public Vector df(final Vector x) {
				final DenseArrayVector v = (DenseArrayVector) f.df(x);
				ArrayVector.negate(v.rawArray());
				return v;
			}
		};
	}
}
