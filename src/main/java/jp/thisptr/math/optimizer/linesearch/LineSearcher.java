package jp.thisptr.math.optimizer.linesearch;

import jp.thisptr.math.optimizer.Function;

public abstract class LineSearcher {
	public abstract double search(final Function f, final double[] x, final double[] dfx, final double[] dir, final double delta0);
}