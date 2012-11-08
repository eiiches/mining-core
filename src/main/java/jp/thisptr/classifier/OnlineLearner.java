package jp.thisptr.classifier;

import jp.thisptr.core.tuple.Pair;

public interface OnlineLearner<VectorType, ClassType> extends Learner<VectorType, ClassType> {
	void learn(final Pair<VectorType, ClassType> data);
}