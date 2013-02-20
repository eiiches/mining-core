package jp.thisptr.classifier;

import jp.thisptr.structure.instance.LabeledInstance;

public interface OnlineLearner<VectorType, LabelType> extends Classifier<VectorType, LabelType> {
	void learn(final LabeledInstance<? extends VectorType, ? extends LabelType> instance);
}