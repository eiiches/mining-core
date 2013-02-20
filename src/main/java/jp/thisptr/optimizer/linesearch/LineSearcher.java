package jp.thisptr.optimizer.linesearch;

import jp.thisptr.optimizer.Function;

public abstract class LineSearcher {
	public abstract double search(final Function f, final double[] x0, final double fx0, final double[] dfx0, final double[] dir, final double delta0);
}