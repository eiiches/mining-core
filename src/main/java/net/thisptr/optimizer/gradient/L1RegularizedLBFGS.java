package net.thisptr.optimizer.gradient;

import net.thisptr.math.operator.ArrayOp;
import net.thisptr.optimizer.Function;
import net.thisptr.optimizer.linesearch.LineSearcher;

abstract class OrthantWiseLineSearcher extends LineSearcher {
	@Override
	public double search(final Function f, final double[] x0, final double fx0, final double[] dfx0, final double[] dir, final double delta0) {
		throw new UnsupportedOperationException();
	}
	public abstract double search(final Function f, final double c, final boolean[] doRegularize, final double[] x0, final double[] pdfx0, final double[] dir, final double delta0);
}

/**
 * Backtracking line search algorithm described in: Galen Andrew and Jianfeng Gao, "Scalable Training of L1-Regularized Log-Linear Models,"
 * In Proc. of International Conference on Machine Learning, Corvallis, OR, 2007.
 * @author eiichi
 */
class OrthantWiseBacktrackingLineSearcher extends OrthantWiseLineSearcher {
	private final double decreaseRate;
	private final int maxIteration;
	private final double alpha;
	
	public OrthantWiseBacktrackingLineSearcher() {
		this(0.5, /* alpha = */ 0.0001, 1000);
	}
	
	public OrthantWiseBacktrackingLineSearcher(final double decreaseRate, final double alpha, final int maxIteration) {
		this.decreaseRate = decreaseRate;
		this.alpha = alpha;
		this.maxIteration = maxIteration;
	}
	
	private static double regularizedFunction(final Function f, final double c, final boolean[] doRegularize, final double[] x) {
		return f.f(x) + c * ArrayOp.l1norm(x, doRegularize);
	}
	
	private static void projectOntoOrthant(final double[] x, final double[] orthant, final boolean[] mask) {
		for (int i = 0; i < x.length; ++i)
			if (mask[i] && x[i] * orthant[i] <= 0.0)
				x[i] = 0.0;
	}
	
	@Override
	public double search(final Function f, final double c, final boolean[] doRegularize, final double[] x0, final double[] pdfx0, final double[] dir, final double delta0) {
		// Initialize step size.
		double delta = delta0;
		
		final double fx0 = regularizedFunction(f, c, doRegularize, x0);
		
		// Select an orthant to explore.
		// Only the signs of the components affect the result.
		final double[] xi0 = new double[f.xdim()];
		for (int i = 0; i < x0.length; ++i)
			xi0[i] = x0[i] != 0 ? x0[i] : -pdfx0[i];
	
		final double[] x = new double[x0.length];	
		for (int loop = 0; loop < maxIteration; ++loop) {
			ArrayOp.addScaled(x, x0, delta, dir);
			projectOntoOrthant(x, xi0, doRegularize);
			
			final double fx = regularizedFunction(f, c, doRegularize, x);
			
			double dxpdfx = 0.0;
			for (int i = 0; i < f.xdim(); ++i)
				dxpdfx += (x[i] - x0[i]) * pdfx0[i];
			if (fx > fx0 + alpha * dxpdfx) {
				delta *= decreaseRate;
				continue;
			}
			
			return delta;
		}
			
		throw new RuntimeException("Backtracking line search could not find solution.");
	}
}

/**
 * Implementation of: Galen Andrew and Jianfeng Gao, "Scalable Training of L1-Regularized Log-Linear Models,"
 * In Proc. of International Conference on Machine Learning, Corvallis, OR, 2007.
 * @author eiichi
 */
public class L1RegularizedLBFGS extends LimitedMemoryBFGS {
	private final boolean[] doRegularize;
	private double[] pdfx; // pseudo-dfx
	
	/**
	 * Regularization constant.
	 */
	private final double c;

	public L1RegularizedLBFGS(final Function f, final double c) {
		this(f, c, null);
	}
	
	public L1RegularizedLBFGS(final Function f, final double c, final double[] x0) {
		this(f, c, x0, 20);
	}
	
	public L1RegularizedLBFGS(final Function f, final double c, final double[] x0, final int updateHistoryLimit) {
		this(f, c, x0, updateHistoryLimit, null);
	}
	
	public L1RegularizedLBFGS(final Function f, final double c, final double[] x0, final int updateHistoryLimit, final boolean[] doRegularize) {
		super(f, x0, new OrthantWiseBacktrackingLineSearcher(), updateHistoryLimit);
		this.c = c;
		this.doRegularize = new boolean[f.xdim()];
		if (doRegularize != null) {
			for (int i = 0; i < f.xdim(); ++i)
				this.doRegularize[i] = doRegularize[i];
		} else {
			for (int i = 0; i < f.xdim(); ++i)
				this.doRegularize[i] = true;
		}
		this.pdfx = makePseudoGradient();
	}
	
	private double[] makePseudoGradient() {
		final double[] result = new double[f.xdim()];
		for (int i = 0; i < f.xdim(); ++i) {
			if (!doRegularize[i]) {
				result[i] = dfx[i];
				continue;
			}
			
			if (0.0 < x[i]) {
				result[i] = dfx[i] + c;
				continue;
			}
			if (x[i] < 0.0) {
				result[i] = dfx[i] - c;
				continue;
			}
	
			assert x[i] == 0.0;

			if (dfx[i] < -c) {
				// Take the right partial derivative.
				result[i] = dfx[i] + c;
				continue;
			}
			if (c < dfx[i]) {
				// Take the left partial derivative.
				result[i] = dfx[i] - c;
				continue;
			}
			result[i] = 0.0;
		}
		return result;
	}
	
	@Override
	protected double[] getSearchDirection0() {
		return ArrayOp.negate(pdfx.clone());
	}

	private void constrainSearchDirection(final double[] dir) {
		for (int i = 0; i < f.xdim(); ++i)
			if (dir[i] * pdfx[i] >= 0)
				dir[i] = 0;
	}
	
	@Override
	protected void updateSearchDirection(final double[] dir) {
		super.updateSearchDirection(dir);
		constrainSearchDirection(dir);
	}

	@Override
	protected double doLineSearch(final double[] dir, final double delta0) {
		return ((OrthantWiseLineSearcher) lineSearcher).search(f, c, doRegularize, x, pdfx, dir, delta0);
	}

	@Override
	public void step() {
		super.step();
		pdfx = makePseudoGradient();
	}
	
	@Override
	public boolean converged(final double epsilon) {
		return ArrayOp.l2norm(pdfx) < epsilon;
	}
}