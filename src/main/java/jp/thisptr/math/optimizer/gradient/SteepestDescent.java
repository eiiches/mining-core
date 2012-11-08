package jp.thisptr.math.optimizer.gradient;

import jp.thisptr.math.optimizer.Function;
import jp.thisptr.math.optimizer.FunctionMinimizer;
import jp.thisptr.math.optimizer.linesearch.BacktrackingLineSearcher;
import jp.thisptr.math.optimizer.linesearch.LineSearcher;
import jp.thisptr.math.vector.d.ArrayVector;
import jp.thisptr.math.vector.d.DenseArrayVector;
import jp.thisptr.math.vector.d.Vector;

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
	
	public SteepestDescent(final Function f, final Vector x0) {
		this(f, x0, null);
	}
	
	public SteepestDescent(final Function f, final Vector x0, final LineSearcher lineSearcher) {
		this.f = f;
		this.x = new double[f.xdim()];
		if (x0 != null)
			for (int i = 0; i < f.xdim(); ++i)
				this.x[i] = x0.get(i);
		this.dfx = ((DenseArrayVector) f.df(DenseArrayVector.wrap(x))).rawArray();
		this.lineSearcher = lineSearcher != null ? lineSearcher : new BacktrackingLineSearcher();
	}

	@Override
	public void step() {
		final double[] d = dfx.clone();
		ArrayVector.negate(d);
		double stepsize = lineSearcher.search(f, x, dfx, d, 1.0);
		
		ArrayVector.addScaled(x, stepsize, d);
		dfx = ((DenseArrayVector) f.df(DenseArrayVector.wrap(x))).rawArray();
	}
	
	@Override
	public boolean converged() {
		return converged(0.00001);
	}

	@Override
	public boolean converged(final double epsilon) {
		return ArrayVector.l2norm(dfx) < epsilon;
	}

	@Override
	public Vector current() {
		return new DenseArrayVector(x);
	}
	
	@Override
	public Function function() {
		return f;
	}
}
