package net.thisptr.optimizer.gradient;

import net.thisptr.math.operator.ArrayOp;
import net.thisptr.optimizer.Function;
import net.thisptr.optimizer.FunctionMinimizer;
import net.thisptr.optimizer.linesearch.BacktrackingLineSearcher;
import net.thisptr.optimizer.linesearch.LineSearcher;

public class SteepestDescent extends FunctionMinimizer {
	private final Function f;
	private final LineSearcher lineSearcher;
	
	private double[] x;
	private double[] dfx;
	
	public SteepestDescent(final Function f) {
		this(f, null, null);
	}
	
	public SteepestDescent(final Function f, final LineSearcher lineSearcher) {
		this(f, null, lineSearcher);
	}
	
	public SteepestDescent(final Function f, final double[] x0) {
		this(f, x0, null);
	}
	
	public SteepestDescent(final Function f, final double[] x0, final LineSearcher lineSearcher) {
		this.f = f;
		this.x = new double[f.xdim()];
		if (x0 != null)
			for (int i = 0; i < f.xdim(); ++i)
				this.x[i] = x0[i];
		this.dfx = f.df(x);
		this.lineSearcher = lineSearcher != null ? lineSearcher : new BacktrackingLineSearcher();
	}

	@Override
	public void step() {
		final double[] dir = ArrayOp.negateNew(dfx);
		final double fx0 = f.f(x);
		final double stepsize = lineSearcher.search(f, x, fx0, dfx, dir, 1.0);
		
		ArrayOp.addScaled(x, stepsize, dir);
		dfx = f.df(x);
	}

	@Override
	public boolean converged(final double epsilon) {
		return ArrayOp.l2norm(dfx) < epsilon;
	}

	@Override
	public double[] current() {
		return x.clone();
	}
	
	@Override
	public Function function() {
		return f;
	}
}
