package jp.thisptr.classifier.online;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class BinaryAROWTest {
	public static class AccuracyTest extends AbstractAccuracyTest {
		@DataPoint
		public static BinaryAROW create() {
			return new BinaryAROW();
		}
	}
}
