package jp.thisptr.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.thisptr.core.exception.UnsupportedTypeException;
import jp.thisptr.core.lambda.Lambda0;
import jp.thisptr.core.tuple.Pair;

public class CrossValidation<VectorType, ClassType> {
	private Lambda0<? extends Learner<VectorType, ClassType>> learnerFactory;
	
	public CrossValidation(final Lambda0<? extends Learner<VectorType, ClassType>> learnerFactory) {
		this.learnerFactory = learnerFactory;
	}
	
	private static <VectorType, ClassType> void learn(final Learner<VectorType, ClassType> learner, final List<Pair<VectorType, ClassType>> learnset) {
		if (learner instanceof BatchLearner) {
			final BatchLearner<VectorType, ClassType> batchLearner = (BatchLearner<VectorType, ClassType>) learner;
			batchLearner.learn(learnset);
		} else if (learner instanceof OnlineLearner) {
			final OnlineLearner<VectorType, ClassType> onlineLearner = (OnlineLearner<VectorType, ClassType>) learner;
			for (final Pair<VectorType, ClassType> data : learnset)
				onlineLearner.learn(data);
		} else {
			throw new UnsupportedTypeException();
		}
	}
	
	public ConfusionMatrix<ClassType> fold(final List<Pair<VectorType, ClassType>> dataset0, final int n) {
		final ConfusionMatrix<ClassType> cv = new ConfusionMatrix<ClassType>();
		final List<Pair<VectorType, ClassType>> dataset = new ArrayList<Pair<VectorType, ClassType>>(dataset0);
		Collections.shuffle(dataset);
		for (int leave = 0; leave < n; ++leave) {
			final List<Pair<VectorType, ClassType>> learnset = new ArrayList<Pair<VectorType, ClassType>>();
			final List<Pair<VectorType, ClassType>> testset = new ArrayList<Pair<VectorType, ClassType>>();
			final double blockSize = dataset.size() / (double) n;
			final double begin = blockSize * leave;
			final double end = blockSize * (leave + 1);
			for (int i = 0; i < dataset.size(); ++i) {
				if (begin <= i && i < end) {
					testset.add(dataset.get(i));
					continue;
				}
				learnset.add(dataset.get(i));
			}
			final Learner<VectorType, ClassType> learner = learnerFactory.invoke();
			learn(learner, learnset);
			for (final Pair<VectorType, ClassType> d : testset) {
				final ClassType classified = learner.classify(d.getFirst());
				cv.add(d.getSecond(), classified);
			}
		}
		return cv;
	}
	
	public ConfusionMatrix<ClassType> loocv(final List<Pair<VectorType, ClassType>> dataset) {
		return fold(dataset, dataset.size());
	}
}