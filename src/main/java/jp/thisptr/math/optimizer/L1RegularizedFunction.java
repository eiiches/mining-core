package jp.thisptr.math.optimizer;

import org.apache.commons.lang.mutable.MutableDouble;

import jp.thisptr.math.structure.vector.Vector;

public class L1RegularizedFunction extends Function {
	private final Function orig;
	
	public L1RegularizedFunction(final Function orig) {
		this.orig = orig;
	}

	@Override
	public int xdim() {
		return orig.xdim();
	}
	
	public Vector regularizeDf(final Vector dfx) {
		
	}
	
	public double regularizeF(final double fx) {
		
	}

	@Override
	public double f(final Vector x) {
		final double unregulerizedF = orig.f(x);
		return regularizeF(unregulerizedF);
	}

	@Override
	public Vector df(final Vector x) {
		final Vector unregularizedDf = orig.df(x);
		return regularizeDf(unregularizedDf);
	}
}
