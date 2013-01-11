package jp.thisptr.classifier.batch;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import jp.thisptr.classifier.evaluate.ConfusionMatrix;
import jp.thisptr.instance.Instance;
import jp.thisptr.instance.LabeledInstance;
import jp.thisptr.math.distribution.Distribution;
import jp.thisptr.math.distribution.GaussianDistribution;
import jp.thisptr.math.structure.vector.SparseMapVector;

import org.junit.Test;

public class BinaryLogisticRegressionTest {
	private SparseMapVector createRandomVector(final Distribution distribution, final int dimension) {
		double[] result = new double[dimension];
		for (int i = 0; i < dimension; ++i)
			result[i] = distribution.sample();
		return new SparseMapVector(result);
	}

	@Test
	public void test() {
		final int dim = 2;
		final GaussianDistribution class1 = new GaussianDistribution(0, 0.5);
		final GaussianDistribution class2 = new GaussianDistribution(1, 0.5);

		final List<LabeledInstance<SparseMapVector, Boolean>> instances = new ArrayList<LabeledInstance<SparseMapVector, Boolean>>();
		for (int i = 0; i < 100; ++i) instances.add(new LabeledInstance<SparseMapVector, Boolean>(createRandomVector(class1, dim), true));
		for (int i = 0; i < 100; ++i) instances.add(new LabeledInstance<SparseMapVector, Boolean>(createRandomVector(class2, dim), false));

		final BinaryLogisticRegression classifier = new BinaryLogisticRegression();
		classifier.learn(instances);
		
		final ConfusionMatrix<Boolean> confusion = new ConfusionMatrix<Boolean>();
		for (final LabeledInstance<SparseMapVector, Boolean> instance : instances)
			confusion.add(instance.getLabel(), classifier.classify(instance.getVector()));

		assertTrue(confusion.getAccuracy() > 0.9);
	}
}
