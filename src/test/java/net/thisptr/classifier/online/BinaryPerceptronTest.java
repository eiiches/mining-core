package net.thisptr.classifier.online;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class BinaryPerceptronTest {
	public static class AccuracyTest extends AbstractAccuracyTest {
		@DataPoint
		public static BinaryPerceptron create() {
			return new BinaryPerceptron();
		}
	}
}
