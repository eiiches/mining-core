package net.thisptr.optimizer;

public abstract class Function {
	public abstract int xdim();
	public abstract double f(final double[] x);
	public abstract double[] df(final double[] x);
}