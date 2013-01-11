package jp.thisptr.classifier.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.thisptr.classifier.Classifier;
import jp.thisptr.instance.LabeledInstance;

public abstract class CrossValidation<VectorType, ClassType> {
	protected abstract Classifier<VectorType, ClassType> build(final List<? extends LabeledInstance<? extends VectorType, ClassType>> learnset);
	
	public <InstanceType extends LabeledInstance<? extends VectorType, ClassType>> ConfusionMatrix<ClassType> fold(final Iterable<InstanceType> dataset0, final int n) {
		final ConfusionMatrix<ClassType> cv = new ConfusionMatrix<ClassType>();
		final List<InstanceType> dataset = new ArrayList<InstanceType>();
		for (final InstanceType instance : dataset0)
			dataset.add(instance);
		Collections.shuffle(dataset);
		
		for (int leave = 0; leave < n; ++leave) {
			final List<InstanceType> learnset = new ArrayList<InstanceType>();
			final List<InstanceType> testset = new ArrayList<InstanceType>();
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
			for (final InstanceType d : testset) {
				final ClassType classified = classifier.classify(d.getVector());
				cv.add(d.getLabel(), classified);
			}
		}
		return cv;
	}
	
	public ConfusionMatrix<ClassType> loocv(final List<? extends LabeledInstance<? extends VectorType, ClassType>> dataset) {
		return fold(dataset, dataset.size());
	}
}