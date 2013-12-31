package net.thisptr.classifier.batch;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.thisptr.classifier.evaluation.ConfusionMatrix;
import net.thisptr.instance.LabeledInstance;
import net.thisptr.math.distribution.Distribution;
import net.thisptr.math.distribution.GaussianDistribution;
import net.thisptr.math.vector.SparseMapVector;

import org.junit.Ignore;
import org.junit.Test;

public class BinaryLogisticRegressionTest {
	private SparseMapVector createRandomVector(final Distribution distribution, final int dimension) {
		double[] result = new double[dimension];
		for (int i = 0; i < dimension; ++i)
			result[i] = distribution.sample();
		return new SparseMapVector(result);
	}

	@Ignore
	@Test
	public void test() {
		final int dim = 2;
		final GaussianDistribution class1 = new GaussianDistribution(0, 0.5);
		final GaussianDistribution class2 = new GaussianDistribution(1, 0.5);

		final List<LabeledInstance<Long, SparseMapVector, Boolean>> instances = new ArrayList<LabeledInstance<Long, SparseMapVector, Boolean>>();
		for (int i = 0; i < 100; ++i) instances.add(new LabeledInstance<Long, SparseMapVector, Boolean>(createRandomVector(class1, dim), true));
		for (int i = 0; i < 100; ++i) instances.add(new LabeledInstance<Long, SparseMapVector, Boolean>(createRandomVector(class2, dim), false));

		final BinaryLogisticRegression classifier = new BinaryLogisticRegression();
		classifier.train(instances);

		final ConfusionMatrix<Boolean> confusion = new ConfusionMatrix<Boolean>();
		for (final LabeledInstance<Long, SparseMapVector, Boolean> instance : instances)
			confusion.add(instance.getLabel(), classifier.classify(instance.getVector()));

		final double accuracy = confusion.getAccuracy();
		assertTrue(accuracy > 0.9);
	}
}
