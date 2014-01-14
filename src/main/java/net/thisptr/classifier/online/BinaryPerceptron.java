package net.thisptr.classifier.online;

import net.thisptr.math.vector.Vector;
import net.thisptr.math.vector.VectorVisitor;


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
	protected boolean doUpdate(final Vector x, final int y) {
		final double wx = calcWx(x);
		
		if (y * wx <= 0.0) {
			w[0] += learningRate * y;
			x.walk(new VectorVisitor() {
				public void visit(final int index, final double value) {
					w[index + 1] += learningRate * y * value;
				}
			});
			return true;
		}
		
		return false;
	}
}
