package jp.thisptr.classifier;

import jp.thisptr.classifier.instance.BasicInstances;

public interface BatchLearner<VectorType, ClassType, InstancesType extends BasicInstances<VectorType, ClassType>> extends Learner<VectorType, ClassType> {
	void learn(final InstancesType dataset);
}