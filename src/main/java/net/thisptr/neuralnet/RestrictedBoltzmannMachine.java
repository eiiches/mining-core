package net.thisptr.neuralnet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import net.thisptr.math.distribution.GaussianDistribution;
import net.thisptr.math.distribution.UniformDistribution;
import net.thisptr.math.operator.MatrixOp;
import net.thisptr.math.vector.DenseArrayVector;
import net.thisptr.math.vector.Vector;
import net.thisptr.math.vector.Vector.Visitor;
import net.thisptr.util.Range;
import net.thisptr.util.ThreadUtils;

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
	private double[][] weights;

	/**
	 * Stores the difference of the weight from the previous update.
	 */
	private double[][] update;

	private DoubleArrayPool doubleArrayPool = new DoubleArrayPool();
	private BooleanArrayPool booleanArrayPool = new BooleanArrayPool();

	public RestrictedBoltzmannMachine(final int visibleNodes, final int hiddenNodes) {
		this(visibleNodes, hiddenNodes, DEFAULT_LEARNING_RATE, DEFAULT_DROP_RATE);
	}

	public RestrictedBoltzmannMachine(final int visibleNodes, final int hiddenNodes, final double learningRate, final double dropRate) {
		this.visibleNodes = visibleNodes;
		this.hiddenNodes = hiddenNodes;
		this.learningRate = learningRate;
		this.dropRate = dropRate;
		this.weights = new double[hiddenNodes + 1][visibleNodes + 1];

		final GaussianDistribution initializer = new GaussianDistribution(0.0, INITIAL_WEIGHT_DEVIATION);
		for (int i = 0; i < visibleNodes + 1; ++i)
			for (int j = 0; j < hiddenNodes + 1; ++j)
				this.weights[j][i] = initializer.sample();
		this.weights[0][0] = 0.0; // this value is unused

		this.update = new double[hiddenNodes + 1][visibleNodes + 1];
	}

	private void activateWithBias(final boolean[] h, final double[] ph) {
		for (int i = 1; i < h.length; ++i)
			h[i] = uniformDistribution.next() < ph[i];
	}

	private void activateWithBias(final boolean[][] h, final double[][] ph) {
		for (int n = 0; n < ph.length; ++n)
			activateWithBias(h[n], ph[n]);
	}

	private static void applyNoiseWithBias(final double[] x, final CachedSampler sampler) {
		for (int i = 1; i < x.length; ++i)
			x[i] += sampler.next();
	}

	private static void applyNoiseWithBias(final double[][] x, final CachedSampler sampler) {
		for (int n = 0; n < x.length; ++n)
			applyNoiseWithBias(x[n], sampler);
	}

	private static void toArrayAddingBias(final double[] result, final Vector h, final int lengthIncludingBias) {
		Arrays.fill(result, 0.0);

		result[0] = 1.0;
		if (h instanceof DenseArrayVector) {
			final double[] raw = ((DenseArrayVector) h).raw();
			System.arraycopy(raw, 0, result, 1, lengthIncludingBias - 1);
		} else {
			h.walk(new Visitor() {
				@Override
				public void visit(int index, double value) {
					result[index + 1] = value;
				}
			});
		}
	}

	private static void applyLogisticSigmoid(final double[] h) {
		for (int j = 0; j < h.length; ++j)
			h[j] = FastLogisticFunction.logistic(h[j]);
	}

	private static void applyLogisticSigmoid(final double[][] h) {
		for (int n = 0; n < h.length; ++n)
			applyLogisticSigmoid(h[n]);
	}

	private void computeHiddenProbabilityWithBias(final double[][] h, final double[][] x, final ExecutorService executor, final int hintNumThreads) {
		MatrixOp.assign_X_Yt(h, x, weights, executor, hintNumThreads);
		applyLogisticSigmoid(h);
		for (int n = 0; n < h.length; ++n)
			h[n][0] = 1.0;
	}

	private void computeHiddenProbabilityWithBias(final double[] h, final double[] x, final ExecutorService executor, final int hintNumThreads) {
		computeHiddenProbabilityWithBias(new double[][] { h }, new double[][] { x }, executor, hintNumThreads);
	}

	private void computeVisibleProbabilityWithBias(final double[][] x, final double[][] h, final ExecutorService executor, final int hintNumThreads) {
		MatrixOp.assign_X_Y(x, h, weights, executor, hintNumThreads);
		applyLogisticSigmoid(x);
		for (int n = 0; n < x.length; ++n)
			x[n][0] = 1.0;
	}

	private void computeVisibleProbabilityWithBias(final double[][] x, final boolean[][] h, final ExecutorService executor, final int hintNumThreads) {
		MatrixOp.assign_X_Y(x, h, weights, executor, hintNumThreads);
		applyLogisticSigmoid(x);
		for (int n = 0; n < x.length; ++n)
			x[n][0] = 1.0;
	}

	private void computeVisibleProbabilityWithBias(final double[] x, final double[] h, final ExecutorService executor, final int hintNumThreads) {
		computeVisibleProbabilityWithBias(new double[][] { x }, new double[][] { h }, executor, hintNumThreads);
	}

	private void computeHiddenValueWithBias(final double[][] h, final double[][] x, final ExecutorService executor, final int hintNumThreads) {
		MatrixOp.assign_X_Yt(h, x, weights, executor, hintNumThreads);
		for (int n = 0; n < h.length; ++n)
			h[n][0] = 1.0;
	}

	private void computeHiddenValueWithBias(final double[] h, final double[] x, final ExecutorService executor, final int hintNumThreads) {
		computeHiddenValueWithBias(new double[][] { h }, new double[][] { x }, executor, hintNumThreads);
	}

	@Override
	public void train(final Vector px0) {
		train(px0, null);
	}

	public void train(final Vector px0, final ExecutorService executor) {
		final double[] x0 = doubleArrayPool.borrowArray(visibleNodes + 1);
		try {
			toArrayAddingBias(x0, px0, visibleNodes + 1);
			trainWithBias(new double[][] { x0 }, executor, Runtime.getRuntime().availableProcessors());
		} finally {
			doubleArrayPool.returnArray(x0);
		}
	}

	@Override
	public void train(final List<Vector> examples) {
		train(examples, null);
	}

	public void train(final List<Vector> examples, final ExecutorService executor) {
		final double[][] _examples = doubleArrayPool.borrowArrays(visibleNodes + 1, examples.size());
		try {
			for (int i = 0; i < examples.size(); ++i)
				toArrayAddingBias(_examples[i], examples.get(i), visibleNodes + 1);
			trainWithBias(_examples, executor, Runtime.getRuntime().availableProcessors());
		} finally {
			doubleArrayPool.returnArrays(_examples);
		}
	}

	private void dropNodesWithBias(final double[] h, final boolean[] drop, final double value) {
		for (int j = 0; j < drop.length; ++j)
			if (drop[j])
				h[j] = value;
	}

	private void dropNodesWithBias(final double[][] hs, final boolean[] drop, final double value) {
		for (int n = 0; n < hs.length; ++n)
			dropNodesWithBias(hs[n], drop, value);
	}

	private void buildDropVector(final boolean[] drop) {
		drop[0] = false; // never drop bias node
		for (int j = 1; j < hiddenNodes + 1; ++j)
			drop[j] = uniformDistribution.next() < dropRate;
	}

	private void trainWithBias(final double[][] x0, final ExecutorService executor, final int hintNumThreads) {
		final int batchSize = x0.length;
		final double[][] ph0 = doubleArrayPool.borrowArrays(hiddenNodes + 1, batchSize);
		final double[][] ph1 = doubleArrayPool.borrowArrays(hiddenNodes + 1, batchSize);
		final double[][] px1 = doubleArrayPool.borrowArrays(visibleNodes + 1, batchSize);
		final boolean[][] h0 = booleanArrayPool.borrowArrays(hiddenNodes + 1, batchSize);
		final double[][] tmp = doubleArrayPool.borrowArrays(visibleNodes + 1, hiddenNodes + 1);

		final boolean[] dropv = booleanArrayPool.borrowArray(hiddenNodes + 1);
		buildDropVector(dropv);

		try {
			switch (hiddenUnitType) {
				case Linear:
					computeHiddenValueWithBias(ph0, x0, executor, hintNumThreads);
					dropNodesWithBias(ph0, dropv, 0);
					applyNoiseWithBias(ph0, normalDistribution);

					computeVisibleProbabilityWithBias(px1, ph0, executor, hintNumThreads);

					computeHiddenValueWithBias(ph1, px1, executor, hintNumThreads);
					dropNodesWithBias(ph1, dropv, 0);
					break;
				case Logistic:
					// 3.1 Updating the hidden states.
					// "It is very important to make these hidden states binary, rather than using the probabilities
					// themselves. If the probabilities are used, each hidden unit can communicate a real-value to the
					// visible units during the reconstruction." [1]
					computeHiddenProbabilityWithBias(ph0, x0, executor, hintNumThreads);
					dropNodesWithBias(ph0, dropv, 0.5);
					activateWithBias(h0, ph0);

					// 3.2 Updating the visible states.
					// "However, it is common to use the probability, pi , instead of sampling a binary value." [1]
					computeVisibleProbabilityWithBias(px1, h0, executor, hintNumThreads);

					// 3.1 Updating the hidden states
					// "When using CDn , only the final update of the hidden units should use the probability." [1]
					computeHiddenProbabilityWithBias(ph1, px1, executor, hintNumThreads);
					dropNodesWithBias(ph1, dropv, 0.5);
					break;
				default:
					throw new IllegalArgumentException("The type of hidden unit is not supported: " + hiddenUnitType);
			}

			// update the weights
			final List<Runnable> tasks = new ArrayList<>();
			for (final Range rowsplit : new Range(0, visibleNodes + 1).split(hintNumThreads)) {
				for (final Range colsplit : new Range(0, hiddenNodes + 1).split(hintNumThreads)) {
					tasks.add(new Runnable() {
						@Override
						public void run() {
							final double[][] update = RestrictedBoltzmannMachine.this.update;
							final double[][] weights = RestrictedBoltzmannMachine.this.weights;
							final double learningRate = RestrictedBoltzmannMachine.this.learningRate;
							final double momentum = RestrictedBoltzmannMachine.this.momentum;
							final double weightDecay = RestrictedBoltzmannMachine.this.weightDecay;
							final int cend = colsplit.end();
							final int rend = rowsplit.end();
							final int _batchSize = batchSize;
							final double invBatchSize = 1.0 / batchSize;

							for (int j = colsplit.begin(); j < cend; ++j) {
								// FIXME: Should I update disabled hidden nodes?
								for (int i = rowsplit.begin(); i < rend; ++i) {
									double positive = 0, negative = 0;
									for (int n = 0; n < _batchSize; ++n) {
										positive += x0[n][i] * ph0[n][j];
										negative += px1[n][i] * ph1[n][j];
									}
									update[j][i] = update[j][i] * momentum + learningRate * ((positive - negative) * invBatchSize - (weights[j][i] * weightDecay));
									weights[j][i] += update[j][i];
								}
							}
						}
					});
				}
			}

			try {
				ThreadUtils.invokeAll(tasks, executor);
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		} finally {
			doubleArrayPool.returnArrays(ph0);
			doubleArrayPool.returnArrays(ph1);
			doubleArrayPool.returnArrays(px1);
			booleanArrayPool.returnArrays(h0);
			doubleArrayPool.returnArrays(tmp);
			booleanArrayPool.returnArray(dropv);
		}
	}

	@Override
	public Vector reduce(final Vector x) {
		return reduce(x, null);
	}

	public Vector reduce(final Vector x, final ExecutorService executor) {
		final double[] _x = doubleArrayPool.borrowArray(visibleNodes + 1);
		final double[] _h = doubleArrayPool.borrowArray(hiddenNodes + 1);

		try {
			toArrayAddingBias(_x, x, visibleNodes + 1);

			switch (hiddenUnitType) {
				case Linear:
					computeHiddenValueWithBias(_h, _x, executor, Runtime.getRuntime().availableProcessors());
					break;
				case Logistic:
					computeHiddenProbabilityWithBias(_h, _x, executor, Runtime.getRuntime().availableProcessors());
					break;
			}

			return DenseArrayVector.wrap(Arrays.copyOfRange(_h, 1, hiddenNodes + 1));
		} finally {
			doubleArrayPool.returnArray(_x);
			doubleArrayPool.returnArray(_h);
		}
	}

	@Override
	public Vector reconstruct(final Vector h) {
		return reconstruct(h, null);
	}

	public Vector reconstruct(final Vector h, final ExecutorService executor) {
		final double[] _h = doubleArrayPool.borrowArray(hiddenNodes + 1);
		final double[] _x = doubleArrayPool.borrowArray(visibleNodes + 1);
		try {
			toArrayAddingBias(_h, h, hiddenNodes + 1);

			computeVisibleProbabilityWithBias(_x, _h, executor, Runtime.getRuntime().availableProcessors());

			return DenseArrayVector.wrap(Arrays.copyOfRange(_x, 1, visibleNodes + 1));
		} finally {
			doubleArrayPool.returnArray(_x);
			doubleArrayPool.returnArray(_h);
		}
	}

	public double[][] weights() {
		return weights;
	}

	public double learningRate() {
		return learningRate;
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
				buf.putDouble(weights[j][i]);

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
		for (int j = 0; j < hiddenNodes; ++j)
			for (int i = 0; i < visibleNodes; ++i)
				weights[j][i] = buf.getDouble();
	}
}