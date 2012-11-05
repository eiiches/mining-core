package jp.thisptr.math.optimizer;

import jp.thisptr.math.vector.d.Vector;

public abstract class FunctionMinimizer {
	public abstract void step();
	
	public abstract boolean converged();
	public abstract boolean converged(final double epsilon);
	
	public abstract Vector current();
	public abstract Function function();
}