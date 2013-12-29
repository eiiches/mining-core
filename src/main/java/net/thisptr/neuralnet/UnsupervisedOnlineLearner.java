package net.thisptr.neuralnet;

import net.thisptr.math.vector.Vector;

public interface UnsupervisedOnlineLearner {
	public void train(final Vector vector);
}
