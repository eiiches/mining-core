package jp.thisptr.classifier.online;

import java.util.Arrays;
import java.util.Map;

import jp.thisptr.math.vector.SparseMapVector;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an implementation of Soft Confidence-Weighted Learning, as described in, Jialei Wang, et al.
 * Exact Soft Confidence-Weighted Learning. In proceedings of the 29th International Conference on Machine Learning. 2012.
 * 
 */
public class BinarySCW extends AbstractBinaryOnlineClassifier {
	private static Logger log = LoggerFactory.getLogger(BinarySCW.class);
	
	public static final double DEFAULT_C = 0.1;
	public static final double DEFAULT_ETA = 1;
	public static final double DEFAULT_INITIAL_VARIANCE = 1.0;
	
	private final double c;
	private final double eta;
	private final double initialVariance;
	
	private double[] sigma;
	
	public BinarySCW() {
		this(DEFAULT_ETA, DEFAULT_C);
	}
	
	public BinarySCW(final double eta, final double c) {
		this(eta, c, DEFAULT_INITIAL_CAPACITY);
	}
	
	public BinarySCW(final double eta, final double c, final int initialCapacity) {
		super(initialCapacity);
		this.c = c;
		this.eta = eta;
		this.initialVariance = DEFAULT_INITIAL_VARIANCE;
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
		final double phi = eta; // FIXME: phi = CDF^-1(eta)
		final double zeta = 1 + phi * phi;
		final double m = y * calcWx(x);
		final double v = calcV(x);
		final double psi = 1 + phi * phi / 2;
		final double alpha = Math.min(c, Math.max(0, 1.0 / (v * zeta) * (-m * psi + Math.sqrt(m * m * Math.pow(phi, 4) / 4 + v * phi * phi * zeta))));
		
		if (alpha != 0) {
			final double u = Math.pow(-alpha * v * phi + Math.sqrt(alpha * alpha * v * v * phi * phi + 4 * v), 2);
			final double beta = alpha * phi / (Math.sqrt(u) + v * alpha * phi);
					
			w[0] += alpha * y * sigma[0];
			sigma[0] -= beta * sigma[0] * sigma[0];
			for (final Map.Entry<Integer, Double> xi : x.rawMap().entrySet()) {
				final int i = xi.getKey();
				final double value = xi.getValue();
				w[i + 1] += alpha * y * sigma[i + 1] * value;
				sigma[i + 1] -= beta * sigma[0] * sigma[0] * value * value;
			}
			
			if (log.isDebugEnabled())
				log.debug(String.format("Variance updated to %s", ArrayUtils.toString(ArrayUtils.subarray(sigma, 0, n + 1))));

			return true;
		}
		
		return false;
	}
}
