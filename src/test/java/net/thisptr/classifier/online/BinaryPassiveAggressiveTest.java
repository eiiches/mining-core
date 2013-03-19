package net.thisptr.classifier.online;

import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class BinaryPassiveAggressiveTest {
	public static class AccuracyTest extends AbstractAccuracyTest {
		@DataPoint
		public static BinaryPassiveAggressive createPA() {
			return new BinaryPassiveAggressive(BinaryPassiveAggressive.Mode.PA);
		}
		
		@DataPoint
		public static BinaryPassiveAggressive createPA1() {
			return new BinaryPassiveAggressive(BinaryPassiveAggressive.Mode.PA_I);
		}
		
		@DataPoint
		public static BinaryPassiveAggressive createPA2() {
			return new BinaryPassiveAggressive(BinaryPassiveAggressive.Mode.PA_II);
		}
	}
}
