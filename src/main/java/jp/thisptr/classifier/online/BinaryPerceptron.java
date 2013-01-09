package jp.thisptr.classifier.online;

import java.util.Map;

import jp.thisptr.math.structure.vector.SparseMapVector;


public class BinaryPerceptron extends AbstractBinaryOnlineClassifier {
	public static final double DEFAULT_LEARNING_RATE = 0.1;
	
	/**
	 * A learning rate. Changing this only causes a resulting weight vector <tt>w</tt> to be multiplied.
	 * For example, if we set a learning rate 10 times larger, the resulting weight vector will be multiplied by 10.
	 * Only affects the result w by a scalar multiple.
	 */
	private double learningRate;
	
	public BinaryPerceptron() {
		this(DEFAULT_LEARNING_RATE);
	}
	
	public BinaryPerceptron(final double learningRate) {
		this(learningRate, DEFAULT_INITIAL_CAPACITY);
	}
	
	public BinaryPerceptron(final double learningRate, final int initialCapacity) {
		super(initialCapacity);
		this.learningRate = learningRate;
	}

	/**
	 * @see #learningRate
	 */
	public double getLearningRate() {
		return learningRate;
	}

	/**
	 * @see #learningRate
	 */
	public void setLearningRate(final double learningRate) {
		this.learningRate = learningRate;
	}

	@Override
	protected boolean doUpdate(final SparseMapVector x, final int y) {
		final double wx = calcWx(x);
		
		if (y * wx <= 0.0) {
			w[0] += learningRate * y;
			for (final Map.Entry<Integer, Double> entry : x.rawMap().entrySet()) {
				final int i = entry.getKey();
				w[i + 1] += learningRate * y * entry.getValue();
			}
			return true;
		}
		
		return false;
	}
}