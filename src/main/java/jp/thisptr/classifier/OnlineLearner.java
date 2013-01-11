package jp.thisptr.classifier;

import jp.thisptr.instance.LabeledInstance;

public interface OnlineLearner<VectorType, ClassType> extends Classifier<VectorType, ClassType> {
	void learn(final LabeledInstance<VectorType, ClassType> instance);
}