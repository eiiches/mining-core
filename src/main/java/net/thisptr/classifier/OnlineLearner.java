package net.thisptr.classifier;

import net.thisptr.structure.instance.LabeledInstance;

public interface OnlineLearner<VectorType, LabelType> extends Classifier<VectorType, LabelType> {
	void train(final LabeledInstance<? extends VectorType, ? extends LabelType> instance);
}