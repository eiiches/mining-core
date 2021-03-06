package net.thisptr.classifier.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.thisptr.classifier.Classifier;
import net.thisptr.instance.LabeledInstance;

public abstract class CrossValidation<VectorType, ClassType> {
	protected abstract <
		InstanceIdType,
		InstanceVectorType extends VectorType,
		InstanceClassType extends ClassType,
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceClassType>
	>
	Classifier<VectorType, ClassType> build(final List<InstanceType> learnset);

	public <
		InstanceIdType,
		InstanceVectorType extends VectorType,
		InstanceClassType extends ClassType,
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceClassType>
	>
	ConfusionMatrix<ClassType> random(final Collection<InstanceType> instances, final int trainingSamples, final int repeatCount) {
		final ConfusionMatrix<ClassType> cm = new ConfusionMatrix<>();
		final List<InstanceType> dataset = new ArrayList<>(instances);

		for (int i = 0; i < repeatCount; ++i) {
			Collections.shuffle(dataset);

			final List<InstanceType> trainset = dataset.subList(0, trainingSamples);
			final List<InstanceType> testset = dataset.subList(trainingSamples, dataset.size());

			final Classifier<VectorType, ClassType> classifier = build(trainset);
			for (final InstanceType test : testset) {
				final ClassType classified = classifier.classify(test.getVector());
				cm.add(test.getLabel(), classified);
			}
		}

		return cm;
	}

	public <
		InstanceIdType,
		InstanceVectorType extends VectorType,
		InstanceClassType extends ClassType,
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceClassType>
	>
	ConfusionMatrix<ClassType> fold(final Iterable<InstanceType> dataset0, final int n) {
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

	public <
		InstanceIdType,
		InstanceVectorType extends VectorType,
		InstanceClassType extends ClassType,
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceClassType>
	>
	ConfusionMatrix<ClassType> loocv(final Iterable<InstanceType> dataset0) {
		final List<InstanceType> list = new ArrayList<InstanceType>();
		for (final InstanceType instance : dataset0)
			list.add(instance);
		return fold(list, list.size());
	}
}