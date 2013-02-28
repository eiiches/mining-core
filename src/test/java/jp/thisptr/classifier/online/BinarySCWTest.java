package jp.thisptr.classifier.online;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class BinarySCWTest {
	public static class AccuracyTest extends AbstractAccuracyTest {
		@DataPoint
		public static BinarySCW create() {
			return new BinarySCW(BinarySCW.Mode.SCW_I, 2.0, 0.5);
		}
	}
}
