package jp.thisptr.math.optimizer.linesearch;

import jp.thisptr.math.optimizer.Function;
import jp.thisptr.math.structure.operation.ArrayOp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BacktrackingLineSearcher extends LineSearcher {
	private static Logger log = LoggerFactory.getLogger(BacktrackingLineSearcher.class);
	
	public static final double DEFAULT_DECREASE_RATE = 0.5;
	public static final double DEFAULT_INCREASE_RATE = 2.1;
	public static final double DEFAULT_ALPHA = 0.0001;
	public static final double DEFAULT_WOLFE = 0.9;
	public static final int DEFAULT_MAX_ITERATION = 1000;
	
	private final double decreaseRate;
	private final double increaseRate;
	
	/**
	 * This value should be within (0, 0.5) interval. The default is 1e-4.
	 */
	private final double alpha;
	
	private final double wolfe;
	
	private final int maxIteration;
	
	public BacktrackingLineSearcher() {
		this(DEFAULT_DECREASE_RATE, DEFAULT_INCREASE_RATE, DEFAULT_ALPHA, DEFAULT_WOLFE, DEFAULT_MAX_ITERATION);
	}
	
	public BacktrackingLineSearcher(final double decreaseRate, final double increaseRate, final double alpha, final double wolfe, final int maxIteration) {
		this.decreaseRate = decreaseRate;
		this.increaseRate = increaseRate;
		this.alpha = alpha;
		this.wolfe = wolfe;
		this.maxIteration = maxIteration;
	}
	
	@Override
	public double search(final Function f, final double[] x0, final double fx0, final double[] dfx0, final double[] dir, final double delta0) {
		double delta = delta0;
		double decreaseDelta = 0.0;
		
		final double gradInSearchDir0 = ArrayOp.dot(dfx0, dir);
		if (gradInSearchDir0 > 0.0)
			throw new RuntimeException("Incorrect search direction.");

		final double[] x = new double[x0.length];
		for (int i = 0; i < maxIteration; ++i) {
			ArrayOp.addScaled(x, x0, delta, dir);
			final double fx = f.f(x);
			if (Double.isInfinite(fx)) {
				delta *= decreaseRate;
				continue;
			}
	
			if (fx > fx0 + alpha * delta * gradInSearchDir0) {
				delta *= decreaseRate;
				continue;
			}
			
			if (delta > decreaseDelta)
				decreaseDelta = delta;
			
			final double[] dfx = f.df(x);
			final double gradInSearchDir = ArrayOp.dot(dfx, dir);
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
		
		if (decreaseDelta != 0.0) {
			//log.info("Wolfe condition (condition on gradient) not met, but the better than throwing all away with the exception.");
			return decreaseDelta;
		}
		
		throw new RuntimeException("Backtracking line search could not find solution.");
	}
}