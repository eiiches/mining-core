package jp.thisptr.classifier.logisticregression;

import java.util.List;

import jp.thisptr.core.tuple.Pair;
import jp.thisptr.math.optimizer.Function;
import jp.thisptr.math.optimizer.FunctionMinimizer;
import jp.thisptr.math.optimizer.gradient.L1RegularizedLBFGS;
import jp.thisptr.math.vector.d.ArrayVector;
import jp.thisptr.math.vector.d.DenseArrayVector;
import jp.thisptr.math.vector.d.SparseVector;
import jp.thisptr.math.vector.d.SparseVector.IndexedValue;
import jp.thisptr.math.vector.d.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryLogisticRegression {
	private final Logger log = LoggerFactory.getLogger(BinaryLogisticRegression.class);
	
	private Vector w;
	private int dim;
	
	private double l1Regularizer;
	private double convergenceCriteria;
	private int maxIteration = 1000;
	
	/**
	 * Class learning ratio.
	 */
//	private double c = 0.5;

	public BinaryLogisticRegression() {
		this(0.1);
	}
	
	public BinaryLogisticRegression(final double l1Regularizer) {
		this(l1Regularizer, 0.00001);
	}

	public BinaryLogisticRegression(final double l1Regularizer, final double convergenceCriteria) {
		this.l1Regularizer = l1Regularizer;
		this.convergenceCriteria = convergenceCriteria;
	}
	
	private static double calcPy1(final Vector x, final Vector w) {
		final double[] ww = ((DenseArrayVector) w).raw();
		double wx = ww[0];
		for (final IndexedValue v : ((SparseVector) x).values()) {
			// Run boundary check because when we have unseed data x,
			// the dimension of x can (almost always) be larger than the learning data.
			if (ww.length > v.getIndex() + 1)
				wx += v.getValue() * ww[v.getIndex() + 1];
		}
		return Math.pow(1 + Math.exp(-wx), -1);
	}
	
	public void learn(final List<Pair<Vector, Boolean>> dataset) {
		int dim_ = 0;
		for (Pair<Vector, Boolean> d : dataset)
			dim_ = Math.max(d.getFirst().dim(), dim_);
		dim = dim_;
		
		final Function f = new Function() {
			@Override
			public int xdim() {
				return dim + 1; // 1 for intercept term
			}
	
			@Override
			public double f(final Vector w) {
				double sum = 0.0;
				for (final Pair<Vector, Boolean> d : dataset) {
					final boolean y = d.getSecond();
					final Vector x = d.getFirst();
					final double p = calcPy1(x, w);
					if (y) {
						sum += Math.log(p);
					} else {
						sum += Math.log(1 - p);
					}
//					sum += 2 * c * y * Math.log(p) + 2 * (1 - c) * (1 - y) * Math.log(1 - p);
				}
				return -sum;
			}
	
			@Override
			public Vector df(final Vector w) {
				final double[] result = new double[dim + 1];
				for (final Pair<Vector, Boolean> d : dataset) {
					final double y = d.getSecond() ? 1.0 : 0.0;
					final Vector x = d.getFirst();
					final double p = calcPy1(x, w);
					result[0] += y - p;
//					for (int i = 0; i < dim; ++i)
//						result[i + 1] += (y - p) * x.get(i);
					for (final IndexedValue v : ((SparseVector) x).values())
						result[v.getIndex() + 1] += (y - p) * v.getValue();
//						result[v.getIndex() + 1] += -2 * (2 * c * y - c - y + 1) * p * v.getValue() + 2 * c * y * v.getValue();
				}
				return DenseArrayVector.wrap(ArrayVector.negate(result));
			}
		};
		
		// do not regularize the intercept term
		final boolean[] mask = new boolean[dim + 1];
		mask[0] = false;
		for (int i = 1; i < mask.length; ++i)
			mask[i] = true;
		
		final FunctionMinimizer minimizer = new L1RegularizedLBFGS(f, l1Regularizer, null, 30, mask);
		
		log.debug(String.format("Initial: f(x) = %.2f", minimizer.function().f(minimizer.current())));
		
		// run optimization
		try {
			int loop = 0;
			do {
				minimizer.step();
				final double fx = minimizer.function().f(minimizer.current());
				if (Double.isNaN(fx))
					throw new ArithmeticException("The value of f(x) is NaN.");
				log.debug(String.format("Iteration: f(x) = %.2f", fx));
				w = minimizer.current();
				if (++loop > maxIteration) {
					// FIXME: Some other type of exception should be used instead.
					throw new ArithmeticException("Maximum iteration limit exceeded.");
				}
			} while (!minimizer.converged(convergenceCriteria));
		} catch (ArithmeticException e) {
			// The implementation seems to get numerically unstable where x is near the solution.
			// FIXME: For now, just ignore exceptions.
			log.warn(e.getMessage());
		}
		
		log.debug(String.format("Converged: f(x) = %.2f", minimizer.function().f(w)));
		log.debug(String.format("# of non-zero elements in w: %d (c = %.2f)", ArrayVector.nonzero(((DenseArrayVector) w).raw(), 1.0e-10), l1Regularizer));
	}
	
	public Boolean predict(final Vector x) {
		if (w == null)
			throw new IllegalStateException("The model must be learned first.");
		return calcPy1(x, w) > 0.5;
	}

	public Vector getWeights() {
		return w;
	}
}