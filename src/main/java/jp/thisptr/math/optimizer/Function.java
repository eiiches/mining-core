package jp.thisptr.math.optimizer;

import jp.thisptr.math.vector.d.Vector;

public abstract class Function {
	public abstract int xdim();
	public abstract double f(final Vector x);
	public abstract Vector df(final Vector x);
}