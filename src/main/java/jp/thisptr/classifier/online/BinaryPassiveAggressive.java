package jp.thisptr.classifier.online;

import java.util.Map;

import jp.thisptr.math.operation.VectorOp;
import jp.thisptr.math.vector.SparseMapVector;

/**
 * Implementation of Passive-Aggressive online classifier, described in,
 * Koby Crammer, et al. Online Passive-Aggressive Algorithms. Journal of Machine Learning Research 7, pp551-585. 2006.
 * 
 */
public class BinaryPassiveAggressive extends AbstractBinaryOnlineClassifier {
	public static final double DEFAULT_AGGRESSIVENESS = 0.1;
	
	/**
	 * An aggressiveness parameter for PA-I, II variant.
	 */
	private double aggressiveness;
	
	public static enum Mode {
		PA,
		PA_I,
		PA_II;
	}
	
	private final Mode mode;
	
	public BinaryPassiveAggressive(final Mode mode) {
		this(mode, DEFAULT_AGGRESSIVENESS);
	}
	
	public BinaryPassiveAggressive(final Mode mode, final double aggressiveness) {
		this(mode, aggressiveness, DEFAULT_INITIAL_CAPACITY);
	}
	
	public BinaryPassiveAggressive(final Mode mode, final double aggressiveness, final int initialCapacity) {
		super(initialCapacity);
		this.aggressiveness = aggressiveness;
		this.mode = mode;
	}

	/**
	 * @see #aggressiveness
	 */
	public double getAggressiveness() {
		return aggressiveness;
	}

	/**
	 * @see #aggressiveness
	 */
	public void setAggressiveness(final double aggressiveness) {
		this.aggressiveness = aggressiveness;
	}
	
	private double calcUpdateFactor(final SparseMapVector x, final int y, final double wx) {
		// adding 1 is for an intercept term, avoiding the case x2 == 0.
		final double x2 = VectorOp.l2norm2(x) + 1;
		final double loss = Math.max(0.0, 1 - y * wx);

		switch (mode) {
		case PA:
			return loss / x2;
		case PA_I:
			return Math.min(aggressiveness, loss / x2);
		case PA_II:
			return loss / (x2 + 1 / (2 * aggressiveness));
		default:
			throw new IllegalArgumentException("Unsupported Passive-Aggressive variant is specified.");
		}
	}
	
	@Override
	protected boolean doUpdate(final SparseMapVector x, final int y) {
		final double wx = calcWx(x);
		
		if (y * wx <= 1.0) {
			final double tau = calcUpdateFactor(x, y, wx);
			w[0] += tau * y;
			for (final Map.Entry<Integer, Double> entry : x.rawMap().entrySet()) {
				final int i = entry.getKey();
				w[i + 1] += tau * y * entry.getValue();
			}
			return true;
		}
		
		return false;
	}
}
