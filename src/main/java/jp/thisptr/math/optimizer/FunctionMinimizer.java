package jp.thisptr.math.optimizer;

import jp.thisptr.math.structure.operation.ArrayOp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class FunctionMinimizer {
	private static Logger log = LoggerFactory.getLogger(FunctionMinimizer.class);
	
	public static final double DEFAULT_EPSILON = 1e-4;
	public static final int DEFAULT_MAX_ITERATIONS = 1000;
	
	public abstract void step();
	
	public boolean converged() {
		return converged(DEFAULT_EPSILON);
	}
	
	public abstract boolean converged(final double epsilon);
	
	public double[] minimize() {
		return minimize(DEFAULT_MAX_ITERATIONS, DEFAULT_EPSILON);
	}
	
	public double[] minimize(final int maxIterations, final double epsilon) {
		if (log.isDebugEnabled())
			log.debug(String.format("Initial: f(%s) = %.3f, f'(x) = %s.", ArrayOp.toString(current()), function().f(current()), ArrayOp.toString(function().df(current()))));
		for (int iterations = 0; iterations < maxIterations; ++iterations) {
			if (converged(epsilon)) {
				if (log.isInfoEnabled())
					log.info(String.format("Converged in %d iterations.", iterations + 1));
				return current();
			}
			step();
			if (log.isDebugEnabled())
				log.debug(String.format("Iteration %d: f(%s) = %.3f, f'(x) = %s.", iterations + 1, ArrayOp.toString(current()), function().f(current()), ArrayOp.toString(function().df(current()))));
		}
		throw new NotConvergedException();
	}
	
	public abstract double[] current();
	public abstract Function function();
}