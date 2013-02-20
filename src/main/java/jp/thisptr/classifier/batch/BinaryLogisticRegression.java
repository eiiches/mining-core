package jp.thisptr.classifier.batch;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

import java.util.List;

import jp.thisptr.classifier.BatchLearner;
import jp.thisptr.math.operation.ArrayOp;
import jp.thisptr.math.vector.DenseArrayVector;
import jp.thisptr.math.vector.SparseMapVector;
import jp.thisptr.math.vector.Vector;
import jp.thisptr.optimizer.Function;
import jp.thisptr.optimizer.FunctionMinimizer;
import jp.thisptr.optimizer.gradient.L1RegularizedLBFGS;
import jp.thisptr.structure.instance.Instances;
import jp.thisptr.structure.instance.LabeledInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryLogisticRegression implements BatchLearner<SparseMapVector, Boolean> {
	private final Logger log = LoggerFactory.getLogger(BinaryLogisticRegression.class);
	
	private static final int DEFAULT_MAX_ITERATION = 1000;
	private static final double DEFAULT_L1REGULARIZER = 0.1;
	private static final double DEFAULT_CONVERGENCE_CRITERIA = 0.00001;
	
	private double l1Regularizer;
	private double convergenceCriteria;
	private int maxIteration;
	
	private double[] w;
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

	private static double calcPy1(final SparseMapVector x, final double[] w) {
		final double[] wx = new double[] { w[0] };
		x.accept(new Vector.Visitor() {
			public void visit(final int index, final double value) {
				// Run boundary check because when we have unseed data x,
				// the dimension of x can (almost always) be larger than the learning data.
				if (w.length > index + 1)
					wx[0] += value * w[index + 1];
			}
		});
		return 1.0 / (1 + Math.exp(-wx[0]));
	}
	
	@Override
	public void learn(final List<? extends LabeledInstance<? extends SparseMapVector, Boolean>> instances) {
		dim = Instances.getDimension(instances);
		
		final Function f = new Function() {
			@Override
			public int xdim() {
				return dim + 1; // 1 for intercept term
			}
	
			@Override
			public double f(final double[] w) {
				double sum = 0.0;
				for (final LabeledInstance<? extends SparseMapVector, Boolean> instance : instances) {
					final boolean y = instance.getLabel();
					final SparseMapVector x = instance.getVector();
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
			public double[] df(final double[] w) {
				final double[] result = new double[dim + 1];
				for (final LabeledInstance<? extends SparseMapVector, Boolean> instance : instances) {
					final double y = instance.getLabel() ? 1.0 : 0.0;
					final SparseMapVector x = instance.getVector();
					final double p = calcPy1(x, w);
					result[0] += y - p;
//					for (int i = 0; i < dim; ++i)
//						result[i + 1] += (y - p) * x.get(i);
					x.accept(new Vector.Visitor() {
						public void visit(final int index, final double value) {
							result[index + 1] += (y - p) * value;
//							result[v.getIndex() + 1] += -2 * (2 * c * y - c - y + 1) * p * v.getValue() + 2 * c * y * v.getValue();
						}
					});
				}
				ArrayOp.negate(result);
				return result;
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
		log.debug(String.format("# of non-zero elements in w: %d (c = %.2f)", ArrayOp.nonzero(w, 1.0e-10), l1Regularizer));
	}
	
	@Override
	public Boolean classify(final SparseMapVector x) {
		if (w == null)
			throw new IllegalStateException("The model must be learned first.");
		// FIXME: The threshold should be better to be able to be adjusted.
		return calcPy1(x, w) > 0.5;
	}

	public DenseArrayVector getWeights() {
		return new DenseArrayVector(w);
	}
}