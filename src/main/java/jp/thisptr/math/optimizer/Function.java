package jp.thisptr.math.optimizer;

public abstract class Function {
	public abstract int xdim();
	public abstract double f(final double[] x);
	public abstract double[] df(final double[] x);
}