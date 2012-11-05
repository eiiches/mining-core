package jp.thisptr.math.optimizer.gradient;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import jp.thisptr.math.distribution.UniformDistribution;
import jp.thisptr.math.optimizer.Function;
import jp.thisptr.math.optimizer.FunctionMinimizer;
import jp.thisptr.math.optimizer.FunctionUtils;
import jp.thisptr.math.optimizer.linesearch.BacktrackingLineSearcher;
import jp.thisptr.math.vector.d.ArrayVector;
import jp.thisptr.math.vector.d.DenseArrayVector;
import jp.thisptr.math.vector.d.Vector;

import org.junit.Test;

public class LimitedMemoryBFGSTest {
	
	private void runOptimization(final String name, final FunctionMinimizer optimizer, final Function target, final double[] expected, final int maxStep) {
		System.out.printf("(%s) Initial: f(%s) = %.2f, f'(x) = %s%n", name, optimizer.current(), target.f(optimizer.current()), target.df(optimizer.current()));
		{ int i = 1; while (!optimizer.converged()) {
			optimizer.step();
			System.out.printf("(%s) Iteration %d: f(%s) = %.2f, f'(x) = %s%n", name, i, optimizer.current(), target.f(optimizer.current()), target.df(optimizer.current()));
			if (i > maxStep)
				throw new RuntimeException("Poor convergence or diverged!");
			++i;
		}}
		System.out.printf("(%s) Converged%n", name);
		assertArrayEquals(expected, ((DenseArrayVector) optimizer.current()).raw(), 0.001);
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
	
		final FunctionMinimizer optimizer = new LimitedMemoryBFGS(target, DenseArrayVector.wrap(x0), new BacktrackingLineSearcher());
		runOptimization("LibBFGSSample", optimizer, target, expected, 10000);
	}
	
	@Test
	public void testSimpleEllipticParaboloid() { for (int loop = 0; loop < 3; ++loop) {
		final double[] expected = new UniformDistribution(-100, 100).sample(100);

		final Function target = new Function() {
			public double f(final Vector x) {
				// y = (x[0] - e[1])^2 + (x[1] - e[1])^2 + ... + (x[n] - e[n])^2
				double result = 0.0;
				for (int i = 0; i < expected.length; ++i)
					result += Math.pow(x.get(i) - expected[i], 2);
				return result;
			}
			public Vector df(final Vector x) {
				// y' = [2 * (x[0] - e[1]), 2 * (x[1] - e[1]), ..., 2 * (x[n] - e[n])]
				final DenseArrayVector result = new DenseArrayVector(expected.length);
				for (int i = 0; i < expected.length; ++i)
					result.set(i, 2 * (x.get(i) - expected[i]));
				return result;
			}
			public int xdim() {
				return expected.length;
			}
		};
		
		final FunctionMinimizer optimizer = new LimitedMemoryBFGS(target);
		runOptimization("Paraboloid", optimizer, target, expected, 1000);
	}}
	
	@Test
	public void testWoods() {
		final double[] expected = new double[] { 1.0, 1.0, 1.0, 1.0 };
		
		final Function target = new Function() {
			@Override
			public int xdim() {
				return 4;
			}

			@Override
			public double f(final Vector v) {
				final double w = v.get(0);
				final double x = v.get(1);
				final double y = v.get(2);
				final double z = v.get(3);
				return 100 * Math.pow(x - w * w, 2)
						+ Math.pow(w - 1, 2)
						+ 90 * Math.pow(z - y * y, 2)
						+ Math.pow(1 - y, 2)
						+ 10.1 * Math.pow(x - 1, 2)
						+ 10.1 * Math.pow(z - 1, 2)
						+ 19.8 * (x - 1) * (z - 1);
			}

			@Override
			public Vector df(final Vector v) {
				final double w = v.get(0);
				final double x = v.get(1);
				final double y = v.get(2);
				final double z = v.get(3);
				return new DenseArrayVector(
						-2 * 2 * 100 * w * (x - w * w) + 2 * (w - 1),
						100 * 2 * (x - w * w) + 10.1 * 2 * (x - 1) + 19.8 * (z - 1),
						90 * 2 * (x - y * y) * (-2 * y) - 2 * (1 - y),
						90 * 2 * (z - y * y) + 10.1 * 2 * (z - 1) + 19.8 * (x - 1)
				);
			}
		};
		
		assertEquals(19192, target.f(new DenseArrayVector(-3.0, -1.0, -3.0, -1.0)), 0.0001);
		assertEquals(0, target.f(new DenseArrayVector(1.0, 1.0, 1.0, 1.0)), 0.0001);
		
		final FunctionMinimizer optimizer = new LimitedMemoryBFGS(target, new DenseArrayVector(-3, -1, -3, -1), new BacktrackingLineSearcher(0.5, 2.1, 0.0, 1.0, 1000), 100);
		runOptimization("Woods", optimizer, target, expected, 1000);
	}
	
	@Test
	public void testFletcherAndPowellHelicalValley() {
		final double[] expected = new double[] { 1.0, 0.0, 0.0 };
		
		final Function target = new Function() {
			public int xdim() {
				return expected.length;
			}
			private double phi(final double x, final double y) {
				if (x > 0) {
					return Math.atan(y / x) / (2 * Math.PI);
				} else if (x < 0) {
					return (Math.PI + Math.atan(y / x)) / (2 * Math.PI);
				}
				return Double.NaN;
			}
			private double dphidx(final double x, final double y) {
				final double result = y / (x * x + y * y) / (2 * Math.PI);
//				System.out.printf("dphidx(x = %.2f, y = %.2f) = %.2f%n", x, y, result);
				return result;
			}
			private double dphidy(final double x, final double y) {
				final double result = x / (x * x + y * y) / (2 * Math.PI);
//				System.out.printf("dphidy(x = %.2f, y = %.2f) = %.2f%n", x, y, result);
				return result;
			}
			public double f(final Vector w) {
				final double x = w.get(0);
				final double y = w.get(1);
				final double z = w.get(2);
				return 100 * (Math.pow(z - 10 * phi(x, y), 2) + Math.pow(Math.sqrt(x * x + y * y) - 1, 2)) + z * z;
			}
			public Vector df(final Vector w) {
				final double x = w.get(0);
				final double y = w.get(1);
				final double z = w.get(2);
				final DenseArrayVector result = new DenseArrayVector(
						100 * ((2 * z - 20 * phi(x, y)) * dphidx(x, y) + 2 * x - 2 * x / Math.sqrt(x * x + y * y)),
						100 * ((2 * z - 20 * phi(x, y)) * dphidy(x, y) + 2 * y - 2 * y / Math.sqrt(x * x + y * y)),
						100 * (2 * z - 20 * phi(x, y)) + 2 * z
				);
//				System.out.printf("df(%.2f, %.2f, %.2f) = %s%n", x, y, z, result);
				return result;
			}
		};

		assertEquals(2500, target.f(new DenseArrayVector(-1.0, 0.0, 0.0)), 0.0001);
		assertEquals(0, target.f(new DenseArrayVector(1.0, 0.0, 0.0)), 0.0001);

		final FunctionMinimizer optimizer = new LimitedMemoryBFGS(target, new DenseArrayVector(-1.0, 0.0, 0.0), new BacktrackingLineSearcher());
		runOptimization("HelicalValley", optimizer, target, expected, 1000);
	}
	
	private static class Instance {
		public final double y;
		public final Vector x;
		public Instance(final Vector x, final double y) {
			this.x = x;
			this.y = y;
		}
	}
	
	@Test
	public void testL2RegularizedLogLinearModel() {
		final double[] expected = new double[] { 0.1, 0.40, -0.07, 0.05, -0.20 };
//		final double[] expected = new double[] { 0.0, 0.40 };
		final Instance[] instances = new Instance[] {
				// new Instance(new DenseArrayVector(intercept, good, bad, exciting, boring), label)
				new Instance(new DenseArrayVector(1, 1, 1, 0, 0), 0.99999999999),
				new Instance(new DenseArrayVector(1, 0, 0, 1, 0), 0.99999999999),
				new Instance(new DenseArrayVector(1, 0, 1, 0, 1), 0.00000000001),
				new Instance(new DenseArrayVector(1, 0, 1, 1, 0), 0.00000000001),
		};
	
		final Function target = FunctionUtils.negate(new Function() {
			public double p(final Vector w, final Vector x) {
				double dot = 0.0;
				for (int i = 0; i < w.dim(); ++i)
					dot += w.get(i) * x.get(i);
				return dot / (dot + 1);
			}
			@Override
			public double f(final Vector w) {
				double result = 0.0;
				for (final Instance instance : instances)
					result += (instance.y) * Math.log(p(w, instance.x)) + (1 - instance.y) * Math.log(1 - p(w, instance.x));
				return result; // TODO: add regularization term
			}
			@Override
			public Vector df(final Vector w) {
				final double[] result = new double[expected.length];
				for (final Instance instance : instances)
					ArrayVector.addScaled(result, instance.y - p(w, instance.x), ((DenseArrayVector) instance.x).raw());
				return DenseArrayVector.wrap(result); // TODO: add regularization term
			}
			@Override
			public int xdim() {
				return expected.length;
			}
		});
		
		final FunctionMinimizer optimizer = new LimitedMemoryBFGS(target, new DenseArrayVector(expected.length), new BacktrackingLineSearcher());
		runOptimization("L2LogLinear", optimizer, target, expected, 200);
	}
}
