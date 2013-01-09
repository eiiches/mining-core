package jp.thisptr.classifier.logisticregression;

import static org.junit.Assert.assertTrue;
import jp.thisptr.classifier.ConfusionMatrix;
import jp.thisptr.classifier.batch.BinaryLogisticRegression;
import jp.thisptr.classifier.instance.Instance;
import jp.thisptr.classifier.instance.Instances;
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

		final Instances<SparseMapVector, Boolean> instances = new Instances<SparseMapVector, Boolean>();
		for (int i = 0; i < 100; ++i) instances.add(createRandomVector(class1, dim), true);
		for (int i = 0; i < 100; ++i) instances.add(createRandomVector(class2, dim), false);

		final BinaryLogisticRegression classifier = new BinaryLogisticRegression();
		classifier.learn(instances);
		
		final ConfusionMatrix<Boolean> confusion = new ConfusionMatrix<Boolean>();
		for (final Instance<SparseMapVector, Boolean> instance : instances)
			confusion.add(instance.getLabel(), classifier.classify(instance.getVector()));

		assertTrue(confusion.getAccuracy() > 0.9);
	}
}
