package net.thisptr.classifier;

import java.util.List;

import net.thisptr.structure.instance.LabeledInstance;

public interface BatchLearner<VectorType, ClassType> extends Classifier<VectorType, ClassType> {
	<
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceClassType>,
		InstanceIdType,
		InstanceVectorType extends VectorType,
		InstanceClassType extends ClassType
	>
	void train(final List<InstanceType> instances);
}