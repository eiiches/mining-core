package net.thisptr.classifier.online;

import java.util.Arrays;

import net.thisptr.math.SpecialFunctions;
import net.thisptr.math.vector.Vector;
import net.thisptr.math.vector.VectorVisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Doubles;

/**
 * This is an implementation of Soft Confidence-Weighted Learning, as described in, Jialei Wang, et al.
 * Exact Soft Confidence-Weighted Learning. In proceedings of the 29th International Conference on Machine Learning. 2012.
 * 
 */
public class BinarySCW extends AbstractBinaryOnlineClassifier {
	private static Logger log = LoggerFactory.getLogger(BinarySCW.class);

	public static final double DEFAULT_C = 0.1;
	public static final double DEFAULT_ETA = 0.8;
	public static final double DEFAULT_INITIAL_VARIANCE = 1.0;
	public static final Mode DEFAULT_MODE = Mode.SCW_I;

	private final Mode mode;
	private final double c;

	/**
	 * A confidence parameter. This value must be within a range of (0, 1).
	 */
	private final double eta;

	/**
	 * A inverse gaussian cumulative of eta.
	 */
	private final double phi;

	private final double initialVariance;

	public static enum Mode {
		SCW_I,
		SCW_II
	}

	private double[] sigma;

	public BinarySCW() {
		this(DEFAULT_MODE, DEFAULT_ETA, DEFAULT_C);
	}

	public BinarySCW(final Mode mode, final double eta, final double c) {
		this(mode, eta, c, DEFAULT_INITIAL_CAPACITY);
	}

	public BinarySCW(final Mode mode, final double eta, final double c, final int initialCapacity) {
		super(initialCapacity);
		this.mode = mode;
		this.c = c;
		this.eta = eta;
		this.phi = SpecialFunctions.gaussianInverseCumulative(eta);
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

	private double calcV(final Vector x) {
		return x.walk(new VectorVisitor() {
			private double result = sigma[0];
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

	@Override
	protected boolean doUpdate(final Vector x, final int y) {
		final double zeta = 1 + phi * phi;
		final double m = y * calcWx(x);
		final double v = calcV(x);
		final double psi = 1 + phi * phi / 2;

		final double alpha = Math.min(c, Math.max(0, 1.0 / (v * zeta) * (-m * psi + Math.sqrt(m * m * Math.pow(phi, 4) / 4 + v * phi * phi * zeta))));
		if (alpha == 0.0)
			return false;

		final double u = Math.pow(-alpha * v * phi + Math.sqrt(alpha * alpha * v * v * phi * phi + 4 * v), 2) / 4;
		final double beta = alpha * phi / (Math.sqrt(u) + v * alpha * phi);

		w[0] += alpha * y * sigma[0];
		sigma[0] -= beta * sigma[0] * sigma[0];
		x.walk(new VectorVisitor() {
			public void visit(final int index, final double value) {
				w[index + 1] += alpha * y * sigma[index + 1] * value;
				sigma[index + 1] -= beta * sigma[index + 1] * sigma[index + 1] * value * value;
			}
		});

		if (log.isDebugEnabled())
			log.debug(String.format("Variance updated to [%s]", Doubles.join(", ", Arrays.copyOfRange(sigma, 0, n + 1))));
		return true;
	}

	public Mode getMode() {
		return mode;
	}
}
