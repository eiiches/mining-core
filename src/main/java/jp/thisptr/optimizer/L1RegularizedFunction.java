package jp.thisptr.optimizer;

import org.apache.commons.lang.NotImplementedException;

import jp.thisptr.math.operation.ArrayOp;

public class L1RegularizedFunction extends Function {
	private final Function f;
	private final double[] regularizers;
	
	public L1RegularizedFunction(final Function f, final double regularizer) {
		this.f = f;
		this.regularizers = ArrayOp.fillNew(regularizer, f.xdim());
	}
	
	public L1RegularizedFunction(final Function f, final double[] regularizers) {
		this.f = f;
		this.regularizers = regularizers.clone();
	}

	@Override
	public int xdim() {
		return f.xdim();
	}
	
	public double[] regularizeDf(final double[] dfx) {
		throw new NotImplementedException();
	}
	
	public double regularizeF(final double fx) {
		throw new NotImplementedException();
	}

	@Override
	public double f(final double[] x) {
		final double unregulerizedF = f.f(x);
		return regularizeF(unregulerizedF);
	}

	@Override
	public double[] df(final double[] x) {
		final double[] unregularizedDf = f.df(x);
		return regularizeDf(unregularizedDf);
	}
}
