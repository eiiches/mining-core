package net.thisptr.math.distribution;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class GaussianDistributionTest {
	public static class GeneralGaussian {
		private GaussianDistribution sut = new GaussianDistribution(3.0, 5.0);
		
		@Test
		public void testDensityAt() {
			assertEquals(0.0666449205783599271305, sut.densityAt(0.0), 1e-10);
			assertEquals(0.0736540280606646615488, sut.densityAt(1.0), 1e-10);
		}
		
		@Test
		public void testCumulativeAt() {
			assertEquals(0.2742531177500735802944, sut.cumulativeAt(0.0), 1e-8);
			assertEquals(0.3445782583896758332631, sut.cumulativeAt(1.0), 1e-8);
			assertEquals(0.0000000000000000000000, sut.cumulativeAt(-100.0), 1e-8);
			assertEquals(1.0000000000000000000000, sut.cumulativeAt(100.0), 1e-8);
		}
		
		@Test
		public void testInverseCumulativeAt() {
			assertEquals(0.0, sut.inverseCumulativeAt(0.2742531177500735802944), 1e-10);
			assertEquals(1.0, sut.inverseCumulativeAt(0.3445782583896758332631), 1e-10);
		}
	}
	
	public static class StandardGaussian {
		private GaussianDistribution sut = new GaussianDistribution(0.0, 1.0);

		@Test
		public void testDensityAt() {
			assertEquals(0.3989422804014326779399, sut.densityAt(0.0), 1e-10);
			assertEquals(0.2419707245191433497978, sut.densityAt(1.0), 1e-10);
		}
		
		@Test
		public void testCumulativeAt() {
			assertEquals(0.5000000000000000000000, sut.cumulativeAt(0.0), 1e-8);
			assertEquals(0.8413447460685429485852, sut.cumulativeAt(1.0), 1e-8);
			assertEquals(0.0000000000000000000000, sut.cumulativeAt(-100.0), 1e-8);
			assertEquals(1.0000000000000000000000, sut.cumulativeAt(100.0), 1e-8);
		}
		
		@Test
		public void testInverseCumulativeAt() {
			assertEquals(0.0, sut.inverseCumulativeAt(0.5000000000000000000000), 1e-10);
			assertEquals(1.0, sut.inverseCumulativeAt(0.8413447460685429485852), 1e-10);
		}
	}
}
