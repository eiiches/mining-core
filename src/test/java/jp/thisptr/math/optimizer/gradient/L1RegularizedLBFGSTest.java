package jp.thisptr.math.optimizer.gradient;

import jp.thisptr.math.optimizer.Function;
import jp.thisptr.math.optimizer.FunctionMinimizer;
import jp.thisptr.math.vector.d.DenseArrayVector;
import jp.thisptr.math.vector.d.Vector;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class L1RegularizedLBFGSTest {
	
	private void runOptimization(final String name, final FunctionMinimizer optimizer, final Function target, final int maxStep) {
		System.out.printf("(%s) Initial: f(%s) = %.2f, f'(x) = %s%n", name, optimizer.current(), target.f(optimizer.current()), target.df(optimizer.current()));
		{ int i = 1; while (!optimizer.converged()) {
			optimizer.step();
			System.out.printf("(%s) Iteration %d: f(%s) = %.2f, f'(x) = %s%n", name, i, optimizer.current(), target.f(optimizer.current()), target.df(optimizer.current()));
			if (i > maxStep)
				throw new RuntimeException("Poor convergence or diverged!");
			++i;
		}}
		System.out.printf("(%s) Converged%n", name);
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
			public double f(final Vector x) {
				double result = 0.0;
				for (int i = 0; i < n; i += 2) {
					double t1 = 1.0 - x.get(i);
					double t2 = 10.0 * (x.get(i + 1) - x.get(i) * x.get(i));
					result += t1 * t1 + t2 * t2;
				}
				return result;
			}
			public Vector df(final Vector x) {
				final double[] result = new double[n];
				for (int i = 0; i < n; i += 2) {
					double t1 = 1.0 - x.get(i);
					double t2 = 10.0 * (x.get(i + 1) - x.get(i) * x.get(i));
					result[i + 1] = 20.0 * t2;
					result[i] = -2.0 * (x.get(i) * result[i + 1] + t1);
				}
				return DenseArrayVector.wrap(result);
			}
			public int xdim() {
				return n;
			}
		};
	
		final double c = 0.1;
		final FunctionMinimizer optimizer = new L1RegularizedLBFGS(target, c, DenseArrayVector.wrap(x0));
		runOptimization("LibBFGSSample", optimizer, target, 10000);
		assertEquals(0.863628, optimizer.current().get(0), 0.0001);
		assertEquals(0.745354, optimizer.current().get(1), 0.0001);
	}
}
