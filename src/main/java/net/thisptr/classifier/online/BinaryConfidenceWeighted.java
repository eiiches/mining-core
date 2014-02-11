package net.thisptr.classifier.online;

import java.util.Arrays;

import net.thisptr.math.SpecialFunctions;
import net.thisptr.math.vector.Vector;
import net.thisptr.math.vector.VectorVisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Doubles;

/**
 * This is an implementation of, Mark Dredze, Koby Crammer, Fernando Pereira. Confidence-Weighted Linear Classification.
 * In proceedings of the 25th International Conference on Machine Learning. 2008.
 * 
 */
public class BinaryConfidenceWeighted extends AbstractBinaryOnlineClassifier {
	private static Logger log = LoggerFactory.getLogger(BinaryConfidenceWeighted.class);
	
	public static final double DEFAULT_ETA = 0.8;
	public static final double DEFAULT_INITIAL_VARIANCE = 1.0;
	
	private final double eta;
	private final double phi;
	private final double initialVariance;
	
	protected double[] sigma;
	
	public BinaryConfidenceWeighted() {
		this(DEFAULT_ETA, DEFAULT_INITIAL_VARIANCE);
	}
	
	public BinaryConfidenceWeighted(final double eta, final double initialVariance) {
		this(eta, initialVariance, DEFAULT_INITIAL_CAPACITY);
	}
	
	public BinaryConfidenceWeighted(final double eta, final double initialVariance, final int initialCapacity) {
		super(initialCapacity);
		this.eta = eta;
		this.phi = SpecialFunctions.gaussianInverseCumulative(eta);
		this.initialVariance = initialVariance;
		this.sigma = new double[initialCapacity];
		Arrays.fill(sigma, initialVariance);
	}
	
	@Override
	protected void doEnsureCapacity(final int newSize) {
		final int oldSize = sigma.length;
		sigma = Arrays.copyOf(sigma, newSize);
		Arrays.fill(sigma, oldSize, newSize, initialVariance);
	}
	
	private double calcV(final Vector x) {
		return x.walk(new VectorVisitor() {
			private double result = 0.0;
			@Override
			public void visit(final int index, final double value) {
				result += sigma[index + 1] * value * value;
			}
			@Override
			public double finish() {
				return result;
			}
		});
	}
	
	/**
	 * Solves a quadratic equation a * x<sup>2</sup> + b * x + c = 0.
	 * @param a
	 * @param b
	 * @param c
	 * @return The larger value of the two solutions found.
	 */
	private static double solveQuadratic(final double a, final double b, final double c) {
		return (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
	}

	@Override
	protected boolean doUpdate(final Vector x, final int y) {
		final double m = y * calcWx(x);
		final double v = calcV(x);
		final double alpha = solveQuadratic(2 * phi, 1 + 2 * phi * m, m - phi * v) / v;
		
		if (alpha > 0) {
			w[0] += alpha * y * sigma[0];
			sigma[0] = 1 / (1 / sigma[0] + 2 * alpha * phi);
			x.walk(new VectorVisitor() {
				public void visit(final int index, final double value) {
					w[index + 1] += alpha * y * sigma[index + 1] * value;
					sigma[index + 1] = 1 / (1 / sigma[index + 1] + 2 * alpha * phi * value * value);
				}
			});
			
			if (log.isDebugEnabled())
				log.debug(String.format("Variance updated to [%s]", Doubles.join(", ", Arrays.copyOfRange(sigma, 0, n + 1))));
			return true;
		}
		
		return false;
	}
}
