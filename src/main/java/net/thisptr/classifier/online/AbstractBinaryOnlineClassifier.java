package net.thisptr.classifier.online;

import java.util.Arrays;

import net.thisptr.classifier.OnlineLearner;
import net.thisptr.math.operator.VectorOp;
import net.thisptr.math.vector.DenseArrayVector;
import net.thisptr.math.vector.Vector;
import net.thisptr.structure.instance.LabeledInstance;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBinaryOnlineClassifier implements OnlineLearner<Vector, Boolean> {
	private static Logger log = LoggerFactory.getLogger(AbstractBinaryOnlineClassifier.class);

	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	
	public AbstractBinaryOnlineClassifier() {
		this(DEFAULT_INITIAL_CAPACITY);
	}
	
	public AbstractBinaryOnlineClassifier(final int initialCapacity) {
		this.n = 0;
		this.w = new double[initialCapacity];
	}
	
	/**
	 * Coefficients. This pointer may be reallocated and is subject to change as the learning progresses.
	 * The first {@link #n} elements are actually used and the rest is allocated for later use.
	 */
	protected double[] w;
	
	/**
	 * Dimension of the learning data, excluding the intercept term.
	 */
	protected int n;
	
	/**
	 * Computes margin wx, a dot product of w and x. This function treat w[0] as an intercept term.
	 * @param x
	 * @return
	 */
	protected double calcWx(final Vector x) {
		// Passing yOffset = 1 and then adding w[0] is for the intercept term.
		return VectorOp.dot(x, w, 1, n) + w[0];
	}
	
	@Override
	public final Boolean classify(final Vector x) {
		final double wx = calcWx(x);
		return wx > 0.0;
	}
	
	private void ensureCapacity(final int size) {
		w = Arrays.copyOf(w, size);
		doEnsureCapacity(size);
	}
	
	private void update(final Vector x, final int y) {
		final boolean isUpdated = doUpdate(x, y);
		if (isUpdated)
			if (log.isDebugEnabled())
				log.debug(String.format("Weight updated to %s", ArrayUtils.toString(ArrayUtils.subarray(w, 0, n + 1))));
	}
	
	@Override
	public final <
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceLabelType>,
		InstanceIdType,
		InstanceVectorType extends Vector,
		InstanceLabelType extends Boolean
	>
	void train(InstanceType instance) {
		final Vector x = instance.getVector();
		final int y = instance.getLabel() ? 1 : -1;
		
		// Ensure capacity for the new learning data.
		while (w.length < x.size() + 1)
			ensureCapacity(w.length * 2);
		if (n < x.size())
			n = x.size();
		
		// Update
		update(x, y);
	}
	
	protected void doEnsureCapacity(final int size) { }
	
	/**
	 * @param x
	 * @param y
	 * @return True if parameter is updated.
	 */
	protected abstract boolean doUpdate(final Vector x, final int y);
	
	public Vector getWeights() {
		return new DenseArrayVector(w);
	}
}