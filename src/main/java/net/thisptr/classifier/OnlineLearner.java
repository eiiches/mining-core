package net.thisptr.classifier;

import net.thisptr.structure.instance.LabeledInstance;

public interface OnlineLearner<VectorType, LabelType> extends Classifier<VectorType, LabelType> {
	<
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceLabelType>,
		InstanceIdType,
		InstanceVectorType extends VectorType,
		InstanceLabelType extends LabelType
	>
	void train(final InstanceType instance);
}