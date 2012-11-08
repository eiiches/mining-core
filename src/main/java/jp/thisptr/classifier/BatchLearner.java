package jp.thisptr.classifier;

import java.util.List;

import jp.thisptr.core.tuple.Pair;

public interface BatchLearner<VectorType, ClassType> extends Learner<VectorType, ClassType> {
	void learn(final List<Pair<VectorType, ClassType>> dataset);
}