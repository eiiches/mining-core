package jp.thisptr.math.optimizer.linesearch;

import jp.thisptr.math.optimizer.Function;
import jp.thisptr.math.vector.d.ArrayVector;
import jp.thisptr.math.vector.d.DenseArrayVector;

public class BacktrackingLineSearcher extends LineSearcher {
	private final double decreaseRate;
	private final double increaseRate;
	
	/**
	 * This value should be within (0, 0.5) interval. The default is 1e-4.
	 */
	private final double alpha;
	
	private final double wolfe;
	
	private final int maxIteration;
	
	public BacktrackingLineSearcher() {
		this(0.5, 2.1, /* alpha = */ 0.0001, /* wolfe = */ 0.9, 1000);
	}
	
	public BacktrackingLineSearcher(final double decreaseRate, final double increaseRate, final double alpha, final double wolfe, final int maxIteration) {
		this.decreaseRate = decreaseRate;
		this.increaseRate = increaseRate;
		this.alpha = alpha;
		this.wolfe = wolfe;
		this.maxIteration = maxIteration;
	}
	
	@Override
	public double search(final Function f, final double[] x0, final double[] dfx0, final double[] dir, final double delta0) {
		double delta = delta0;
		
		final double fx0 = f.f(DenseArrayVector.wrap(x0));
	
		final double gradInSearchDir0 = ArrayVector.dot(dfx0, dir);
		if (gradInSearchDir0 > 0.0)
			throw new RuntimeException("Incorrect search direction.");

		final double[] x = new double[x0.length];
		for (int i = 0; i < maxIteration; ++i) {
			ArrayVector.addScaled(x, x0, delta, dir);
			final double fx = f.f(DenseArrayVector.wrap(x));
	
			if (fx > fx0 + alpha * delta * gradInSearchDir0) {
				delta *= decreaseRate;
				continue;
			}
			
			final double[] dfx = ((DenseArrayVector) f.df(DenseArrayVector.wrap(x))).rawArray();
			final double gradInSearchDir = ArrayVector.dot(dfx, dir);
			if (gradInSearchDir < wolfe * gradInSearchDir0) {
				delta *= increaseRate;
				continue;
			}
			
			if (gradInSearchDir > -wolfe * gradInSearchDir0) {
				delta *= decreaseRate;
				continue;
			}
			
			return delta;
		}
		
		throw new RuntimeException("Backtracking line search could not find solution.");
	}
}