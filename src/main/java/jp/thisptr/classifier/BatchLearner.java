package jp.thisptr.classifier;

import java.util.List;

import jp.thisptr.structure.instance.LabeledInstance;

public interface BatchLearner<VectorType, ClassType> extends Classifier<VectorType, ClassType> {
	void learn(final List<? extends LabeledInstance<? extends VectorType, ClassType>> instances);
}