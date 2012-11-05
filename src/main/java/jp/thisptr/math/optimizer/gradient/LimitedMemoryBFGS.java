package jp.thisptr.math.optimizer.gradient;

import java.util.LinkedList;
import java.util.ListIterator;

import jp.thisptr.math.optimizer.Function;
import jp.thisptr.math.optimizer.FunctionMinimizer;
import jp.thisptr.math.optimizer.linesearch.BacktrackingLineSearcher;
import jp.thisptr.math.optimizer.linesearch.LineSearcher;
import jp.thisptr.math.vector.d.ArrayVector;
import jp.thisptr.math.vector.d.DenseArrayVector;
import jp.thisptr.math.vector.d.Vector;

/**
 * Implementation of: Jorge Nocedal, "Update Quasi-Newton Matrices With Limited Storage,"
 * Mathematics of Computation, Vol. 35, July 1980, pp. 773-782.
 * @author eiichi
 */
public class LimitedMemoryBFGS extends FunctionMinimizer {
	protected static class Update {
		public final double[] s;
		public final double[] y;
		public final double ys;
		public Update(final double[] s, final double[] y, final double sy) {
			this.s = s;
			this.y = y;
			this.ys = sy;
		}
	}
	
	protected final Function f;
	protected final LineSearcher lineSearcher;
	
	// s[i-1], s[i-2], ..., s[i-m]
	protected LinkedList<Update> updateHistory = new LinkedList<Update>();
	
	protected final int updateHistoryLimit;
	protected double[] x;
	protected double[] dfx;
	
	public LimitedMemoryBFGS(final Function f) {
		this(f, null);
	}
	
	public LimitedMemoryBFGS(final Function f, final Vector x0) {
		this(f, x0, null);
	}
	
	public LimitedMemoryBFGS(final Function f, final Vector x0, final LineSearcher lineSearcher) {
		this(f, x0, lineSearcher, 10);
	}
	
	public LimitedMemoryBFGS(final Function f, final Vector x0, final LineSearcher lineSearcher, final int updateHistoryLimit) {
		this.f = f;
		this.x = new double[f.xdim()];
		if (x0 != null)
			for (int i = 0; i < f.xdim(); ++i)
				this.x[i] = x0.get(i);
		this.dfx = ((DenseArrayVector) f.df(DenseArrayVector.wrap(x))).raw();
		this.lineSearcher = lineSearcher != null ? lineSearcher : new BacktrackingLineSearcher();
		this.updateHistoryLimit = updateHistoryLimit;
	}
	
	protected double[] getSearchDirection0() {
		return ArrayVector.negate(dfx.clone());
	}
	
	protected void updateSearchDirection(final double[] dir) {
		final int m = updateHistory.size();
	
		// Scale H[-m] based on the most recent change in the gradient.
		// H[-m] = s[-1]^t y[-1] / y[-1]^t H[-m] y[-1]
		final double hessianScaler = updateHistory.isEmpty() ? 1.0 / ArrayVector.l2norm(dfx)
				: (updateHistory.peekFirst().ys) / ArrayVector.dot(updateHistory.peekFirst().y, updateHistory.peekFirst().y);
		if (Double.isNaN(hessianScaler))
			throw new ArithmeticException("Hessian scaler is NaN.");
		
		final double[] alpha = new double[m];
		
		{
			int i = 0;
			final ListIterator<Update> iter = updateHistory.listIterator();
			while (iter.hasNext()) {                                  // for (i = 1...m):
				final Update u = iter.next();
				alpha[i] = ArrayVector.dot(u.s, dir) / u.ys;          //   alpha[i] = p[-i] s[-i]' q
				ArrayVector.subScaled(dir, alpha[i], u.y);            //   q -= alpha[i] * y
				++i;
			}
		}
		
		ArrayVector.mul(dir, hessianScaler);                          // r = H[-m] * q
		
		{
			int i = m - 1;
			final ListIterator<Update> iter = updateHistory.listIterator(m);
			while (iter.hasPrevious()) {                              // for (i = 1...m):
				final Update u = iter.previous();
				double beta = ArrayVector.dot(u.y, dir) / u.ys;       //   beta = p[-i] y[-i]' r
				ArrayVector.addScaled(dir, alpha[i] - beta, u.s);     //   r += (alpha[i] - beta) s[i]
				--i;
			}
		}
	}
	
	protected double doLineSearch(final double[] dir, final double delta0) {
		return lineSearcher.search(f, x, dfx, dir, delta0);
	}

	@Override
	public void step() {
		// compute: dir = H f'(x)
		final double[] dir = getSearchDirection0(); // Initialize dir = -f'(x)
		updateSearchDirection(dir);

		// The stepsize, which is a negative value, is decided using line search algorithm.
		final double stepsize = doLineSearch(dir, 1.0);

		// update x
		final double[] s = ArrayVector.mulNew(dir, stepsize); // s = stepsize * r
		ArrayVector.add(x, s); // x += s

		// update dfx
		final double[] dfxOld = dfx;
		dfx = ((DenseArrayVector) f.df(DenseArrayVector.wrap(x))).raw();
		final double[] y = ArrayVector.subNew(dfx, dfxOld); // y = dfx - dfxOld

		// store result for last m values at maximum
		final double ys = ArrayVector.dot(s, y);
		updateHistory.addFirst(new Update(s, y, ys));
		if (updateHistory.size() > updateHistoryLimit)
			updateHistory.removeLast();
	}
	
	@Override
	public boolean converged(final double epsilon) {
		return ArrayVector.l2norm(dfx) < epsilon;
	}

	@Override
	public boolean converged() {
		return converged(0.00001);
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