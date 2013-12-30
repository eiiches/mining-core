package net.thisptr.classifier.online;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.thisptr.classifier.OnlineLearner;
import net.thisptr.classifier.evaluation.ConfusionMatrix;
import net.thisptr.math.vector.SparseMapVector;
import net.thisptr.structure.instance.LabeledInstance;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Theories.class)
public abstract class AbstractAccuracyTest {
	private static Logger log = LoggerFactory.getLogger(AbstractAccuracyTest.class);
	
	protected static class Fixture {
		protected static LabeledInstance<Long, SparseMapVector, Boolean> toInstance(final double x1, final double x2, final boolean y) {
			return new LabeledInstance<Long, SparseMapVector, Boolean>(new SparseMapVector(new double[] { x1, x2 }), y);
		}
		public int nIterations = 10;
		public double expectedAccuracy = 0.9;
		public List<LabeledInstance<Long, SparseMapVector, Boolean>> instances;
	}
	
	@DataPoint
	public static Fixture createFixture() {
		return new Fixture() {{
			nIterations = 20;
			expectedAccuracy = 0.9;
			instances = Arrays.asList(
					toInstance(0, 0, true),
					toInstance(0, 1, true),
					toInstance(1, 2, true),
					toInstance(3, 1, false),
					toInstance(2, 1, false),
					toInstance(1, 1, false),
					toInstance(2, 0, false),
					toInstance(1, 0, false)
			);
		}};
	}

	@Theory
	public void testAccuracy(final OnlineLearner<SparseMapVector, Boolean> sut, final Fixture fixture) {
		for (int i = 0; i < fixture.nIterations; ++i) {
			log.debug(String.format("Iteration %d:", i + 1));
			for (final LabeledInstance<Long, SparseMapVector, Boolean> instance : fixture.instances)
				sut.train(instance);
		}

		final ConfusionMatrix<Boolean> cm = new ConfusionMatrix<Boolean>();
		for (final LabeledInstance<Long, SparseMapVector, Boolean> instance : fixture.instances) {
			final Boolean predicted = sut.classify(instance.getVector());
			final Boolean actual = instance.getLabel();
			cm.add(actual, predicted);
		}

		log.info(String.format("fMeasure(TRUE): %.2f", cm.getFMeasure(true)));
		log.info(String.format("fMeasure(FALSE): %.2f", cm.getFMeasure(false)));
		log.info(String.format("Accuracy: %.2f", cm.getAccuracy()));
		
		assertTrue(cm.getAccuracy() >= fixture.expectedAccuracy);
	}
}
