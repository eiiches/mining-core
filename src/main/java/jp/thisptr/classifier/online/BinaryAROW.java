package jp.thisptr.classifier.online;

import java.util.Arrays;
import java.util.Map;

import jp.thisptr.math.structure.vector.SparseMapVector;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of AROW, as described in, Koby Crammer, et al. Adaptive Regularization of Weight Vectors.
 * In proceedings of Neural Information Processing Systems (NIPS). 2009.
 * 
 */
public class BinaryAROW extends AbstractBinaryOnlineClassifier {
	private static Logger log = LoggerFactory.getLogger(BinaryAROW.class);
	
	public static final double DEFAULT_REGULARIZATION_TRADEOFF = 0.1;
	public static final double DEFAULT_INITIAL_VARIANCE = 100;
	
	/**
	 * A tradeoff parameter between the loss and the regularization term, which corresponds to λ<sub>1</sub> and λ<sub>2</sub> in the paper.
	 * It is said that the performance of the classifier is not affected much by this value.
	 */
	private final double regularizationTradeoff;
	private final double initialVariance;
	
	private double[] sigma;
	
	public BinaryAROW() {
		this(DEFAULT_REGULARIZATION_TRADEOFF, DEFAULT_INITIAL_VARIANCE);
	}
	
	public BinaryAROW(final double regularizationTradeoff, final double initialVariance) {
		this(regularizationTradeoff, initialVariance, DEFAULT_INITIAL_CAPACITY);
	}
	
	public BinaryAROW(final double regularizationTradeoff, final double initialVariance, final int initialCapacity) {
		super(initialCapacity);
		this.regularizationTradeoff = regularizationTradeoff;
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
	
	private double calcV(final SparseMapVector x) {
		double result = sigma[0];
		for (final Map.Entry<Integer, Double> xi : x.rawMap().entrySet()) {
			final int i = xi.getKey();
			final double value = xi.getValue();
			result += sigma[i + 1] * value * value;
		}
		return result;
	}
	
	@Override
	protected boolean doUpdate(final SparseMapVector x, final int y) {
		final double wx = calcWx(x);
		final double v = calcV(x);
		final double r = 1 / (2 * regularizationTradeoff);
		final double loss = 1 - y * wx;
		
		if (loss > 0) {
			final double beta = 1 / (v + r);
			final double alpha = loss * beta;
			
			w[0] += alpha * y * sigma[0];
			sigma[0] = 1 / (1 / sigma[0] + 1 / r);
			for (final Map.Entry<Integer, Double> xi : x.rawMap().entrySet()) {
				final int i = xi.getKey();
				final double value = xi.getValue();
				w[i + 1] += alpha * y * sigma[i + 1] * value;
				sigma[i + 1] = 1 / (1 / sigma[i + 1] + value * value / r);
			}
			
			if (log.isDebugEnabled())
				log.debug(String.format("Variance updated to %s", ArrayUtils.toString(ArrayUtils.subarray(sigma, 0, n + 1))));
			
			return true;
		}
		
		return false;
	}
}
