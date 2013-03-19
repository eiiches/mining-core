package net.thisptr.classifier.evaluation;

import static org.junit.Assert.assertEquals;

import net.thisptr.classifier.evaluation.ConfusionMatrix;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class ConfusionMatrixTest {
	public static class WhenEmpty {
		private ConfusionMatrix<Boolean> sut = null;

		@Before
		public void setUp() {
			sut = new ConfusionMatrix<Boolean>();
		}
		
		@Test
		public void testCounts() {
			assertEquals(0, sut.getCount(true, true));
			assertEquals(0, sut.getCount(true, false));
			assertEquals(0, sut.getCount(false, true));
			assertEquals(0, sut.getCount(false, false));
			assertEquals(0, sut.getCountActual(true));
			assertEquals(0, sut.getCountActual(false));
			assertEquals(0, sut.getCountPredicted(true));
			assertEquals(0, sut.getCountPredicted(false));
		}

		@Test
		public void testAccuracy() {
			assertEquals(1.0, sut.getAccuracy(), 0.0);

			assertEquals(1.0, sut.getPrecision(true), 0.0);
			assertEquals(1.0, sut.getRecall(true), 0.0);
			assertEquals(1.0, sut.getFMeasure(true), 0.0);

			assertEquals(1.0, sut.getPrecision(false), 0.0);
			assertEquals(1.0, sut.getRecall(false), 0.0);
			assertEquals(1.0, sut.getFMeasure(false), 0.0);
		}
	}
	
	public static class WhenNotEmpty {
		private ConfusionMatrix<Boolean> sut = null;
		
		@Before
		public void setUp() {
			sut = new ConfusionMatrix<Boolean>();
			sut.add(true, false);
			sut.add(true, true);
		}
		
		@Test
		public void testCounts() {
			assertEquals(1, sut.getCount(true, true));
			assertEquals(1, sut.getCount(true, false));
			assertEquals(0, sut.getCount(false, true));
			assertEquals(0, sut.getCount(false, false));
			assertEquals(2, sut.getCountActual(true));
			assertEquals(0, sut.getCountActual(false));
			assertEquals(1, sut.getCountPredicted(true));
			assertEquals(1, sut.getCountPredicted(false));
		}
		
		@Test
		public void testAccuracy() {
			assertEquals(0.5, sut.getAccuracy(), 0.0);
			
			assertEquals(1.0, sut.getPrecision(true), 0.0);
			assertEquals(0.5, sut.getRecall(true), 0.0);
			assertEquals(2.0 / 3.0, sut.getFMeasure(true), 0.0);
			
			assertEquals(0.0, sut.getPrecision(false), 0.0);
			assertEquals(1.0, sut.getRecall(false), 0.0);
			assertEquals(0.0, sut.getFMeasure(false), 0.0);
		}
	}
}
