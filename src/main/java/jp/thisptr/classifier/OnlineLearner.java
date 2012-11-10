package jp.thisptr.classifier;

import jp.thisptr.classifier.instance.Instance;

public interface OnlineLearner<VectorType, ClassType> extends Learner<VectorType, ClassType> {
	void learn(final Instance<VectorType, ClassType> instance);
}