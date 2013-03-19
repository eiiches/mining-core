package net.thisptr.optimizer.gradient;

import java.util.LinkedList;
import java.util.ListIterator;

import net.thisptr.math.operation.ArrayOp;
import net.thisptr.optimizer.Function;
import net.thisptr.optimizer.FunctionMinimizer;
import net.thisptr.optimizer.linesearch.BacktrackingLineSearcher;
import net.thisptr.optimizer.linesearch.LineSearcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of: Jorge Nocedal, "Update Quasi-Newton Matrices With Limited Storage,"
 * Mathematics of Computation, Vol. 35, July 1980, pp. 773-782.
 * @author eiichi
 */
public class LimitedMemoryBFGS extends FunctionMinimizer {
	private static Logger log = LoggerFactory.getLogger(LimitedMemoryBFGS.class);
	
	protected static class Update {
		public final double[] s;
		public final double[] y;
		public final double ys;
		public final double yy;
		public Update(final double[] s, final double[] y, final double sy, final double yy) {
			this.s = s;
			this.y = y;
			this.ys = sy;
			this.yy = yy;
		}
	}
	
	protected final Function f;
	protected final LineSearcher lineSearcher;
	
	// s[i-1], s[i-2], ..., s[i-m]
	protected LinkedList<Update> updateHistory = new LinkedList<Update>();
	
	protected final int updateHistoryLimit;
	protected double[] x;
	protected double[] dfx;
	protected double stepsize;
	
	public LimitedMemoryBFGS(final Function f) {
		this(f, null);
	}
	
	public LimitedMemoryBFGS(final Function f, final double[] x0) {
		this(f, x0, null);
	}
	
	public LimitedMemoryBFGS(final Function f, final double[] x0, final LineSearcher lineSearcher) {
		this(f, x0, lineSearcher, 10);
	}
	
	public LimitedMemoryBFGS(final Function f, final double[] x0, final LineSearcher lineSearcher, final int updateHistoryLimit) {
		this.f = f;
		this.x = new double[f.xdim()];
		if (x0 != null)
			for (int i = 0; i < f.xdim(); ++i)
				this.x[i] = x0[i];
		this.dfx = f.df(x);
		this.lineSearcher = lineSearcher != null ? lineSearcher : new BacktrackingLineSearcher();
		this.updateHistoryLimit = updateHistoryLimit;
		this.stepsize = 1.0;
	}
	
	protected double[] getSearchDirection0() {
		return ArrayOp.negate(dfx.clone());
	}
	
	protected void updateSearchDirection(final double[] dir) {
		final int m = updateHistory.size();
	
		// Scale H[-m] based on the most recent change in the gradient.
		// H[-m] = s[-1]^t y[-1] / y[-1]^t H[-m] y[-1]
		final double hessianScaler = updateHistory.isEmpty() ? 1.0 / ArrayOp.l2norm(dfx)
				: updateHistory.peekFirst().ys / updateHistory.peekFirst().yy;
//		if (Double.isNaN(hessianScaler))
//			throw new ArithmeticException("Hessian scaler is NaN.");
		
		final double[] alpha = new double[m];
		
		{
			int i = 0;
			final ListIterator<Update> iter = updateHistory.listIterator();
			while (iter.hasNext()) {                                  // for (i = 1...m):
				final Update u = iter.next();
				alpha[i] = ArrayOp.dot(u.s, dir) / u.ys;          //   alpha[i] = p[-i] s[-i]' q
				ArrayOp.subScaled(dir, alpha[i], u.y);            //   q -= alpha[i] * y
				++i;
			}
		}
		
		ArrayOp.mul(dir, hessianScaler);                          // r = H[-m] * q
		
		{
			int i = m - 1;
			final ListIterator<Update> iter = updateHistory.listIterator(m);
			while (iter.hasPrevious()) {                              // for (i = 1...m):
				final Update u = iter.previous();
				double beta = ArrayOp.dot(u.y, dir) / u.ys;       //   beta = p[-i] y[-i]' r
				ArrayOp.addScaled(dir, alpha[i] - beta, u.s);     //   r += (alpha[i] - beta) s[i]
				--i;
			}
		}
	}
	
	protected double doLineSearch(final double[] dir, final double delta0) {
		final double fx0 = f.f(x);
		return lineSearcher.search(f, x, fx0, dfx, dir, delta0);
	}

	@Override
	public void step() {
		// compute: dir = H f'(x)
		final double[] dir = getSearchDirection0(); // Initialize dir = -f'(x)
		updateSearchDirection(dir);

		// The stepsize, which is a negative value, is decided using line search algorithm.
		// TODO: If we use the initial step size from the previous step, or some multiple of the previous step,
		// won't it reduce the computation drastically?
		try {
			stepsize = doLineSearch(dir, 1.0);
		} catch (RuntimeException e) {
			log.warn(e.getMessage() + " Restaring.");
			updateHistory.clear();
			return;
		}

		// update x
		final double[] s = ArrayOp.mulNew(dir, stepsize); // s = stepsize * r
		ArrayOp.add(x, s); // x += s

		// update dfx
		final double[] dfxOld = dfx;
		dfx = f.df(x);
		final double[] y = ArrayOp.subNew(dfx, dfxOld); // y = dfx - dfxOld

		// store result for last m values at maximum
		final double ys = ArrayOp.dot(s, y);
		final double yy = ArrayOp.dot(y, y);
		if (ys == 0.0 || yy == 0.0) {
			log.info("L-BFGS curvature error. Restarting.");
			updateHistory.clear();
		} else {
			updateHistory.addFirst(new Update(s, y, ys, yy));
			if (updateHistory.size() > updateHistoryLimit)
				updateHistory.removeLast();
		}
	}
	
	@Override
	public boolean converged(final double epsilon) {
		return ArrayOp.absmax(dfx) < epsilon;
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
