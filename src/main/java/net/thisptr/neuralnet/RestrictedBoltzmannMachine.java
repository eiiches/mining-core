package net.thisptr.neuralnet;

import java.nio.ByteBuffer;
import java.util.List;

import net.thisptr.math.distribution.GaussianDistribution;
import net.thisptr.math.distribution.UniformDistribution;
import net.thisptr.math.factory.DefaultMathFactory;
import net.thisptr.math.factory.MathFactory;
import net.thisptr.math.matrix.Matrix;
import net.thisptr.math.operator.MathOperator;
import net.thisptr.math.vector.Vector;
import net.thisptr.math.vector.Vector.Visitor;

/**
 * An implementation of Restricted Boltzmann Machine.
 * <p>
 * <b>References:</b><br />
 * <ol>
 * <li>G. E. Hinton, A Practical Guide to Training Restricted Boltzmann Machine.</li>
 * </ol>
 * </p>
 */
public class RestrictedBoltzmannMachine implements DimensionReduction, UnsupervisedOnlineLearner, UnsupervisedOnlineBatchLearner, SerializableModel {
	public static final double DEFAULT_LEARNING_RATE = 0.01;
	public static final double DEFAULT_WEIGHT_DECAY = 0.001;
	public static final double DEFAULT_MOMENTUM = 0.5;
	public static final UnitType DEFAULT_HIDDEN_UNIT_TYPE = UnitType.Logistic;
	public static final double DEFAULT_DROP_RATE = 0.5;
	public static final int DEFAULT_GIBBS_STEPS = 1;

	public static final double INITIAL_WEIGHT_DEVIATION = 0.1;

	private static final CachedSampler uniformDistribution = new CachedSampler(65536, new UniformDistribution(0.0, 1.0));
	private static final CachedSampler normalDistribution = new CachedSampler(65536, new GaussianDistribution(0.0, 1.0));

	/**
	 * The learning rate of RBM.
	 */
	private double learningRate = DEFAULT_LEARNING_RATE;

	/**
	 * Drop some part of the hidden nodes randomly. This is known as dropout.
	 */
	private double dropRate = DEFAULT_DROP_RATE;

	/**
	 * In each iteration, this value times the value of weight is subtracted to penalize large weights.
	 */
	private double weightDecay = DEFAULT_WEIGHT_DECAY;

	/**
	 * In each iteration, this value times the previous update is added to each weight for faster training.
	 */
	private double momentum = DEFAULT_MOMENTUM;

	/**
	 * The number of Gibbs steps to perform to collect statistics.
	 */
	private int gibbsSteps = DEFAULT_GIBBS_STEPS;

	/**
	 * The number of visible nodes without the bias.
	 */
	private int visibleNodes;

	/**
	 * The number of hidden nodes without the bias.
	 */
	private int hiddenNodes;

	public enum UnitType {
		Logistic, Linear
	}

	/**
	 * The type of hidden unit.
	 */
	private UnitType hiddenUnitType = DEFAULT_HIDDEN_UNIT_TYPE;

	/**
	 * <p>
	 * Weights of the network.
	 * </p>
	 * 
	 * Layout: (Transposed)
	 * 
	 * <pre>
	 *        bias      h1       h2
	 *  bias   NaN    w[0][1]  w[0][2]
	 *  v1   w[1][0]  w[1][1]  w[1][2]
	 *  v2   w[2][0]  w[2][1]  w[2][2]
	 *  v3   w[3][0]  w[3][1]  w[3][2]
	 * </pre>
	 */
	private Matrix weights;

	/**
	 * Stores the difference of the weight from the previous update.
	 */
	private Matrix update;

	private BooleanArrayPool booleanArrayPool = new BooleanArrayPool();

	private MathFactory mathFactory;
	private MathOperator mathOperator;

	public RestrictedBoltzmannMachine(final int visibleNodes, final int hiddenNodes) {
		this(visibleNodes, hiddenNodes, DEFAULT_LEARNING_RATE, DEFAULT_DROP_RATE);
	}

	public RestrictedBoltzmannMachine(final int visibleNodes, final int hiddenNodes, final double learningRate, final double dropRate) {
		this(visibleNodes, hiddenNodes, learningRate, dropRate, new DefaultMathFactory());
	}

	public RestrictedBoltzmannMachine(final int visibleNodes, final int hiddenNodes, final double learningRate, final double dropRate, final MathFactory mathFactory) {
		this.mathFactory = mathFactory;
		this.mathOperator = mathFactory.newMathOperator();
		this.matrixPool = new MatrixPool(mathFactory);

		this.visibleNodes = visibleNodes;
		this.hiddenNodes = hiddenNodes;
		this.learningRate = learningRate;
		this.dropRate = dropRate;
		this.weights = mathFactory.newDenseMatrix(hiddenNodes + 1, visibleNodes + 1);

		final GaussianDistribution initializer = new GaussianDistribution(0.0, INITIAL_WEIGHT_DEVIATION);
		for (int j = 0; j < hiddenNodes + 1; ++j)
			for (int i = 0; i < visibleNodes + 1; ++i)
				this.weights.set(j, i, initializer.sample());
		this.weights.set(0, 0, 0.0); // this value is unused
		this.update = mathFactory.newDenseMatrix(hiddenNodes + 1, visibleNodes + 1);
	}

	private void activateWithBias(final Vector h, final Vector ph) {
		for (int i = 1; i < h.size(); ++i)
			h.set(i, uniformDistribution.next() < ph.get(i) ? 1.0 : 0.0);
	}

	private void activateWithBias(final Matrix h, final Matrix ph) {
		for (int n = 0; n < ph.rows(); ++n)
			activateWithBias(h.row(n), ph.row(n));
	}

	private static void applyNoiseWithBias(final Vector x, final CachedSampler sampler) {
		for (int i = 1; i < x.size(); ++i)
			x.set(i, x.get(i) + sampler.next());
	}

	private static void applyNoiseWithBias(final Matrix x, final CachedSampler sampler) {
		for (int n = 0; n < x.rows(); ++n)
			applyNoiseWithBias(x.row(n), sampler);
	}

	private void toArrayAddingBias(final Vector result, final Vector h, final int lengthIncludingBias) {
		mathOperator.assignZero(result);

		h.walk(new Visitor() {
			@Override
			public void visit(int index, double value) {
				if (index + 1 >= result.size())
					return; // feature was not present in training.
				result.set(index + 1, value);
			}
		});
		result.set(0, 1.0);
	}

	private static void applyLogisticSigmoid(final Vector h) {
		for (int j = 0; j < h.size(); ++j)
			h.set(j, FastLogisticFunction.logistic(h.get(j)));
	}

	private static void applyLogisticSigmoid(final Matrix h) {
		for (int n = 0; n < h.rows(); ++n)
			applyLogisticSigmoid(h.row(n));
	}

	private void computeHiddenProbabilityWithBias(final Matrix h, final Matrix x) {
		mathOperator.assignMultiply(h, x, weights.transpose());
		applyLogisticSigmoid(h);
		for (int n = 0; n < h.rows(); ++n)
			h.set(n, 0, 1.0);
	}

	private void computeVisibleProbabilityWithBias(final Matrix x, final Matrix h) {
		mathOperator.assignMultiply(x, h, weights);
		applyLogisticSigmoid(x);
		for (int n = 0; n < x.rows(); ++n)
			x.set(n, 0, 1.0);
	}

	private void computeHiddenValueWithBias(final Matrix h, final Matrix x) {
		mathOperator.assignMultiply(h, x, weights.transpose());
		for (int n = 0; n < h.rows(); ++n)
			h.set(n, 0, 1.0);
	}

	private MatrixPool matrixPool;

	@Override
	public void train(final Vector px0) {
		final Matrix x0 = matrixPool.acquire(1, visibleNodes + 1);
		try {
			toArrayAddingBias(x0.row(0), px0, visibleNodes + 1);
			trainWithBias(x0);
		} finally {
			matrixPool.release(x0);
		}
	}

	@Override
	public void train(final List<Vector> examples) {
		final Matrix _examples = matrixPool.acquire(examples.size(), visibleNodes + 1);
		try {
			for (int i = 0; i < examples.size(); ++i)
				toArrayAddingBias(_examples.row(i), examples.get(i), visibleNodes + 1);
			trainWithBias(_examples);
		} finally {
			matrixPool.release(_examples);
		}
	}

	private void dropNodesWithBias(final Vector h, final boolean[] drop, final double value) {
		for (int j = 1; j < drop.length; ++j)
			if (drop[j])
				h.set(j, value);
	}

	private void dropNodesWithBias(final Matrix hs, final boolean[] drop, final double value) {
		for (int n = 0; n < hs.rows(); ++n)
			dropNodesWithBias(hs.row(n), drop, value);
	}

	private void buildDropVector(final boolean[] drop) {
		drop[0] = false; // never drop bias node
		for (int j = 1; j < hiddenNodes + 1; ++j)
			drop[j] = uniformDistribution.next() < dropRate;
	}

	private void trainWithBias(final Matrix x0) {
		final int batchSize = x0.rows();
		final Matrix ph0 = matrixPool.acquire(batchSize, hiddenNodes + 1);
		final Matrix px1 = matrixPool.acquire(batchSize, visibleNodes + 1);
		final Matrix h0 = matrixPool.acquire(batchSize, hiddenNodes + 1);

		Matrix ph1_m = matrixPool.acquire(batchSize, hiddenNodes + 1);
		Matrix ph1_s = matrixPool.acquire(batchSize, hiddenNodes + 1);

		final boolean[] dropv = booleanArrayPool.borrowArray(hiddenNodes + 1);
		buildDropVector(dropv);

		try {
			switch (hiddenUnitType) {
				case Linear:
					computeHiddenValueWithBias(ph0, x0);
					dropNodesWithBias(ph0, dropv, 0);
					applyNoiseWithBias(ph0, normalDistribution);

					computeVisibleProbabilityWithBias(px1, ph0);

					computeHiddenValueWithBias(ph1_m, px1);
					dropNodesWithBias(ph1_m, dropv, 0);
					break;

				case Logistic:
					// 3.1 Updating the hidden states.
					// "It is very important to make these hidden states binary, rather than using the probabilities
					// themselves. If the probabilities are used, each hidden unit can communicate a real-value to the
					// visible units during the reconstruction." [1]
					computeHiddenProbabilityWithBias(ph0, x0);
					dropNodesWithBias(ph0, dropv, 0.5);

					Matrix p_ph0 = ph0;
					Matrix p_ph1 = ph1_m;

					for (int i = 0; i < gibbsSteps; ++i) {
						// 3.2 Updating the visible states.
						// "However, it is common to use the probability, pi , instead of sampling a binary value." [1]
						activateWithBias(h0, p_ph0);
						computeVisibleProbabilityWithBias(px1, h0);

						// 3.1 Updating the hidden states
						// "When using CDn , only the final update of the hidden units should use the probability." [1]
						computeHiddenProbabilityWithBias(p_ph1, px1);
						dropNodesWithBias(p_ph1, dropv, 0.5);

						if (i == 0) {
							p_ph0 = p_ph1;
							p_ph1 = ph1_s;
						} else {
							// swap
							final Matrix tmp = p_ph0;
							p_ph0 = p_ph1;
							p_ph1 = tmp;
						}
					}

					ph1_m = p_ph0;
					ph1_s = p_ph1;

					break;

				default:
					throw new IllegalArgumentException("The type of hidden unit is not supported: " + hiddenUnitType);
			}

			// update weights
			final Matrix ph1 = ph1_m;
			mathOperator.assignMultiply(update, update, momentum);
			mathOperator.addMultiply(update, ph0.transpose(), x0, learningRate / batchSize);
			mathOperator.addMultiply(update, ph1.transpose(), px1, -learningRate / batchSize);
			mathOperator.addMultiply(update, weights, -learningRate * weightDecay);
			mathOperator.add(weights, update);
		} finally {
			matrixPool.release(ph0, ph1_m, ph1_s, px1, h0);
			booleanArrayPool.returnArray(dropv);
		}
	}

	@Override
	public Vector reduce(final Vector x) {
		final Matrix _x = matrixPool.acquire(1, visibleNodes + 1);
		final Matrix _h = matrixPool.acquire(1, hiddenNodes + 1);

		try {
			toArrayAddingBias(_x.row(0), x, visibleNodes + 1);

			switch (hiddenUnitType) {
				case Linear:
					computeHiddenValueWithBias(_h, _x);
					break;
				case Logistic:
					computeHiddenProbabilityWithBias(_h, _x);
					break;
			}

			final Vector result = mathFactory.newDenseVector(hiddenNodes);
			mathOperator.copyElements(result, 0, _h.row(0), 1, hiddenNodes);
			return result;
		} finally {
			matrixPool.release(_x, _h);
		}
	}

	@Override
	public Vector reconstruct(final Vector h) {
		final Matrix _x = matrixPool.acquire(1, visibleNodes + 1);
		final Matrix _h = matrixPool.acquire(1, hiddenNodes + 1);
		try {
			toArrayAddingBias(_h.row(0), h, hiddenNodes + 1);

			computeVisibleProbabilityWithBias(_x, _h);

			final Vector result = mathFactory.newDenseVector(visibleNodes);
			mathOperator.copyElements(result, 0, _x.row(0), 1, visibleNodes);
			return result;
		} finally {
			matrixPool.release(_x, _h);
		}
	}

	public Matrix weights() {
		return weights;
	}

	public void setDropRate(double dropRate) {
		this.dropRate = dropRate;
	}

	public double getDropRate() {
		return dropRate;
	}

	public int visibleNodes() {
		return visibleNodes;
	}

	public int hiddenNodes() {
		return hiddenNodes;
	}

	public void setHiddenUnitType(final UnitType unitType) {
		this.hiddenUnitType = unitType;
	}

	public UnitType getHiddenUnitType() {
		return hiddenUnitType;
	}

	public void setLearningRate(final double learningRate) {
		this.learningRate = learningRate;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setGibbsSteps(final int gibbsSteps) {
		this.gibbsSteps = gibbsSteps;
	}

	public int getGibbsSteps() {
		return gibbsSteps;
	}

	public ByteBuffer serialize() {
		final ByteBuffer _buf = ByteBuffer.allocateDirect(4 * 3 + 8 * 3 + 8 * (hiddenNodes + 1) * (visibleNodes + 1));

		final ByteBuffer buf = _buf.duplicate();
		buf.putInt(hiddenUnitType.ordinal());
		buf.putInt(visibleNodes);
		buf.putInt(hiddenNodes);
		buf.putDouble(momentum);
		buf.putDouble(learningRate);
		buf.putDouble(weightDecay);
		for (int j = 0; j < hiddenNodes + 1; ++j)
			for (int i = 0; i < visibleNodes + 1; ++i)
				buf.putDouble(weights.get(j, i));

		return _buf;
	}

	public void load(final ByteBuffer _buf) {
		final ByteBuffer buf = _buf.duplicate();
		hiddenUnitType = UnitType.values()[buf.getInt()];
		visibleNodes = buf.getInt();
		hiddenNodes = buf.getInt();
		momentum = buf.getDouble();
		learningRate = buf.getDouble();
		weightDecay = buf.getDouble();
		for (int j = 0; j < hiddenNodes + 1; ++j)
			for (int i = 0; i < visibleNodes + 1; ++i)
				weights.set(j, i, buf.getDouble());
	}
}