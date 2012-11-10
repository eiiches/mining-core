package jp.thisptr.classifier;

import jp.thisptr.classifier.instance.BasicInstances;
import jp.thisptr.classifier.instance.Instance;

public abstract class CrossValidation<VectorType, ClassType> {
	protected abstract Classifier<VectorType, ClassType> build(final BasicInstances<VectorType, ClassType> learnset);
	
	public ConfusionMatrix<ClassType> fold(final Iterable<Instance<VectorType, ClassType>> dataset0, final int n) {
		final ConfusionMatrix<ClassType> cv = new ConfusionMatrix<ClassType>();
		final BasicInstances<VectorType, ClassType> dataset = new BasicInstances<VectorType, ClassType>(dataset0);
		dataset.shuffle();
		for (int leave = 0; leave < n; ++leave) {
			final BasicInstances<VectorType, ClassType> learnset = new BasicInstances<VectorType, ClassType>();
			final BasicInstances<VectorType, ClassType> testset = new BasicInstances<VectorType, ClassType>();
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
			final Classifier<VectorType, ClassType> classifier = build(learnset);
			for (final Instance<VectorType, ClassType> d : testset) {
				final ClassType classified = classifier.classify(d.getVector());
				cv.add(d.getLabel(), classified);
			}
		}
		return cv;
	}
	
	public ConfusionMatrix<ClassType> loocv(final BasicInstances<VectorType, ClassType> dataset) {
		return fold(dataset, dataset.size());
	}
}