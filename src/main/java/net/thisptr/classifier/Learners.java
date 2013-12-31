package net.thisptr.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.thisptr.instance.LabeledInstance;

public class Learners {
	private Learners() {}

	public static <
		LearnerType extends OnlineLearner<VectorType, LabelType>,
		InstanceIdType,
		InstanceVectorType extends VectorType,
		InstanceLabelType extends LabelType,
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceLabelType>,
		VectorType,
		LabelType
	>
	LearnerType train(final LearnerType learner, final List<InstanceType> instances, final int iterations) {
		final List<InstanceType> _instances = new ArrayList<>(instances);
		for (int i = 0; i < iterations; ++i) {
			Collections.shuffle(_instances);
			for (final InstanceType instance : _instances)
				learner.train(instance);
		}
		return learner;
	}

	public static <
		LearnerType extends BatchLearner<VectorType, LabelType>,
		InstanceIdType,
		InstanceVectorType extends VectorType,
		InstanceLabelType extends LabelType,
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceLabelType>,
		VectorType,
		LabelType
	>
	LearnerType train(final LearnerType learner, final List<InstanceType> instances) {
		learner.train(instances);
		return learner;
	}
}