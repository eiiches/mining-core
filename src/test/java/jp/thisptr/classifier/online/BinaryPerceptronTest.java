package jp.thisptr.classifier.online;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import jp.thisptr.classifier.OnlineLearner;
import jp.thisptr.classifier.evaluate.ConfusionMatrix;
import jp.thisptr.instance.LabeledInstance;
import jp.thisptr.math.structure.vector.SparseMapVector;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BinaryPerceptronTest {
	private static Logger log = LoggerFactory.getLogger(BinaryPerceptronTest.class);
	
	private static LabeledInstance<SparseMapVector, Boolean> instance(final double x1, final double x2, final boolean y) {
		return new LabeledInstance<SparseMapVector, Boolean>(new SparseMapVector(x1, x2), y);
	}
	
	private static List<LabeledInstance<SparseMapVector, Boolean>> instances = Arrays.asList(
		instance(0, 0, true),
		instance(0, 1, true),
		instance(1, 2, true),
		instance(3, 1, false),
		instance(2, 1, false),
		instance(1, 1, false),
		instance(2, 0, false),
		instance(1, 0, false)
	);
	
	private static int nIterations = 10;
	
	public OnlineLearner<SparseMapVector, Boolean> doCreateLearner() {
		return new BinaryPerceptron();
	}
	
	@Test
	public void test() {
		final OnlineLearner<SparseMapVector, Boolean> model = doCreateLearner();
		
		for (int i = 0; i < nIterations; ++i) {
			log.debug(String.format("Iteration %d:", i + 1));
			for (final LabeledInstance<SparseMapVector, Boolean> instance : instances)
				model.learn(instance);
		}
	
		final ConfusionMatrix<Boolean> cm = new ConfusionMatrix<Boolean>();
		for (final LabeledInstance<SparseMapVector, Boolean> instance : instances) {
			final Boolean predicted = model.classify(instance.getVector());
			final Boolean actual = instance.getLabel();
			cm.add(actual, predicted);
		}
	
		log.info(String.format("fMeasure(TRUE): %.2f", cm.getFMeasure(true)));
		log.info(String.format("fMeasure(FALSE): %.2f", cm.getFMeasure(false)));
		log.info(String.format("Accuracy: %.2f", cm.getAccuracy()));
		
		assertEquals(1.0, cm.getAccuracy(), 0.1);
	}
}
