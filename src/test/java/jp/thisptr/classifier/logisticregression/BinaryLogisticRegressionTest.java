package jp.thisptr.classifier.logisticregression;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import jp.thisptr.core.tuple.Pair;
import jp.thisptr.math.distribution.GaussianDistribution;
import jp.thisptr.math.vector.d.DenseArrayVector;
import jp.thisptr.math.vector.d.SparseMapVector;
import jp.thisptr.math.vector.d.Vector;

import org.junit.Test;

public class BinaryLogisticRegressionTest {
//
//	@Test
//	public void test() {
//		final int dim = 2;
//		final GaussianDistribution class1 = new GaussianDistribution(0, 0.5);
//		final GaussianDistribution class2 = new GaussianDistribution(1, 0.5);
//
//		final List<Pair<SparseMapVector, Boolean>> dataset = new ArrayList<Pair<SparseMapVector, Boolean>>();
//		for (int i = 0; i < 100; ++i)
//			dataset.add(Pair.make((SparseMapVector) new DenseArrayVector(class1.sample(dim)), true));
//		for (int i = 0; i < 100; ++i)
//			dataset.add(Pair.make((SparseMapVector) new DenseArrayVector(class2.sample(dim)), false));
//
//		BinaryLogisticRegression classifier = new BinaryLogisticRegression();
//		try {
//			classifier.learn(dataset);
//		} catch (Exception e) { }
//
//		int error = 0;
//		for (final Pair<Vector, Boolean> d : dataset) {
//			final boolean predicted = classifier.classify(d.getFirst());
//			if (predicted ^ d.getSecond())
//				++error;
//		}
//
//		assertTrue(error / (double) dataset.size() < 0.1);
//	}
}
