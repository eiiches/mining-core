package jp.thisptr.classifier.batch;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import jp.thisptr.classifier.BatchLearner;
import jp.thisptr.classifier.instance.Instance;
import jp.thisptr.classifier.instance.Instances;
import jp.thisptr.math.optimizer.Function;
import jp.thisptr.math.optimizer.FunctionMinimizer;
import jp.thisptr.math.optimizer.gradient.L1RegularizedLBFGS;
import jp.thisptr.math.structure.operation.ArrayOp;
import jp.thisptr.math.structure.vector.DenseArrayVector;
import jp.thisptr.math.structure.vector.SparseMapVector;
import jp.thisptr.math.structure.vector.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryLogisticRegression implements BatchLearner<SparseMapVector, Boolean, Instances<SparseMapVector, Boolean>> {
	private final Logger log = LoggerFactory.getLogger(BinaryLogisticRegression.class);
	
	private static final int DEFAULT_MAX_ITERATION = 1000;
	private static final double DEFAULT_L1REGULARIZER = 0.1;
	private static final double DEFAULT_CONVERGENCE_CRITERIA = 0.00001;
	
	private double l1Regularizer;
	private double convergenceCriteria;
	private int maxIteration;
	
	private Vector w;
	private int dim;

	public BinaryLogisticRegression() {
		this(DEFAULT_L1REGULARIZER);
	}
	
	public BinaryLogisticRegression(final double l1Regularizer) {
		this(l1Regularizer, DEFAULT_CONVERGENCE_CRITERIA);
	}

	public BinaryLogisticRegression(final double l1Regularizer, final double convergenceCriteria) {
		this.l1Regularizer = l1Regularizer;
		this.convergenceCriteria = convergenceCriteria;
		this.maxIteration = DEFAULT_MAX_ITERATION;
	}

	private static double calcPy1(final SparseMapVector x, final DenseArrayVector w) {
		final double[] ww = w.rawArray();
		double wx = ww[0];
		for (final Int2DoubleMap.Entry v : x.rawMap().int2DoubleEntrySet()) {
			// Run boundary check because when we have unseed data x,
			// the dimension of x can (almost always) be larger than the learning data.
			if (ww.length > v.getIntKey() + 1)
				wx += v.getDoubleValue() * ww[v.getIntKey() + 1];
		}
		return 1.0 / (1 + Math.exp(-wx));
	}
	
	@Override
	public void learn(final Instances<SparseMapVector, Boolean> instances) {
		dim = instances.dim();
		
		final Function f = new Function() {
			@Override
			public int xdim() {
				return dim + 1; // 1 for intercept term
			}
	
			@Override
			public double f(final Vector w) {
				final DenseArrayVector ww = (DenseArrayVector) w;
				double sum = 0.0;
				for (final Instance<SparseMapVector, Boolean> instance : instances) {
					final boolean y = instance.getLabel();
					final SparseMapVector x = instance.getVector();
					final double p = calcPy1(x, ww);
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
				final DenseArrayVector ww = (DenseArrayVector) w;
				final double[] result = new double[dim + 1];
				for (final Instance<SparseMapVector, Boolean> instance : instances) {
					final double y = instance.getLabel() ? 1.0 : 0.0;
					final SparseMapVector x = instance.getVector();
					final double p = calcPy1(x, ww);
					result[0] += y - p;
//					for (int i = 0; i < dim; ++i)
//						result[i + 1] += (y - p) * x.get(i);
					for (final Int2DoubleMap.Entry v : x.rawMap().int2DoubleEntrySet())
						result[v.getIntKey() + 1] += (y - p) * v.getDoubleValue();
//						result[v.getIndex() + 1] += -2 * (2 * c * y - c - y + 1) * p * v.getValue() + 2 * c * y * v.getValue();
				}
				return DenseArrayVector.wrap(ArrayOp.negate(result));
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
		log.debug(String.format("# of non-zero elements in w: %d (c = %.2f)", ArrayOp.nonzero(((DenseArrayVector) w).rawArray(), 1.0e-10), l1Regularizer));
	}
	
	@Override
	public Boolean classify(final SparseMapVector x) {
		if (w == null)
			throw new IllegalStateException("The model must be learned first.");
		// FIXME: The threshold should be better to be able to be adjusted.
		return calcPy1(x, (DenseArrayVector) w) > 0.5;
	}

	public Vector getWeights() {
		return w;
	}
}