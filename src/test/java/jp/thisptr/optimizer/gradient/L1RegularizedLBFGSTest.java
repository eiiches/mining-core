package jp.thisptr.optimizer.gradient;

import static org.junit.Assert.assertEquals;
import jp.thisptr.optimizer.Function;
import jp.thisptr.optimizer.FunctionMinimizer;
import jp.thisptr.optimizer.gradient.L1RegularizedLBFGS;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L1RegularizedLBFGSTest {
	private static Logger log = LoggerFactory.getLogger(L1RegularizedLBFGSTest.class);
	
	private void runOptimization(final String name, final FunctionMinimizer optimizer, final Function target, final int maxIterations, final double epsilon) {
		log.info(String.format("%s: Start", name));
		
		optimizer.minimize(maxIterations, epsilon);
		log.info(String.format("%s: Converged", name));
	}
	
	@Test
	public void testSimpleLibBFGSSample() {
		final int n = 100;
	
		final double[] expected = new double[n];
		for (int i = 0; i < n; ++i)
			expected[i] = 1.0;
		
		final double[] x0 = new double[n];
		for (int i = 0; i < n; i += 2) {
			x0[i] = -1.2;
			x0[i + 1] = 1.0;
		}
		
		final Function target = new Function() {
			public double f(final double[] x) {
				double result = 0.0;
				for (int i = 0; i < n; i += 2) {
					double t1 = 1.0 - x[i];
					double t2 = 10.0 * (x[i + 1] - x[i] * x[i]);
					result += t1 * t1 + t2 * t2;
				}
				return result;
			}
			public double[] df(final double[] x) {
				final double[] result = new double[n];
				for (int i = 0; i < n; i += 2) {
					double t1 = 1.0 - x[i];
					double t2 = 10.0 * (x[i + 1] - x[i] * x[i]);
					result[i + 1] = 20.0 * t2;
					result[i] = -2.0 * (x[i] * result[i + 1] + t1);
				}
				return result;
			}
			public int xdim() {
				return n;
			}
		};
		
		final double c = 0.1;
		final FunctionMinimizer optimizer = new L1RegularizedLBFGS(target, c, x0);
		runOptimization("LibBFGSSample", optimizer, target, 10000, 1e-4);
		assertEquals(0.863628, optimizer.current()[0], 0.0001);
		assertEquals(0.745354, optimizer.current()[1], 0.0001);
	}
}
