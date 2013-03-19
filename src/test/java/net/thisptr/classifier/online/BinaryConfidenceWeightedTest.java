package net.thisptr.classifier.online;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class BinaryConfidenceWeightedTest {
	public static class AccuracyTest extends AbstractAccuracyTest {
		@DataPoint
		public static BinaryConfidenceWeighted create() {
			return new BinaryConfidenceWeighted(0.8, 1);
		}
	}
}
