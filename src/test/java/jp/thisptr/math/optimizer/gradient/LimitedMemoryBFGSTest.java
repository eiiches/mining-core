package jp.thisptr.math.optimizer.gradient;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import jp.thisptr.math.distribution.UniformDistribution;
import jp.thisptr.optimizer.Function;
import jp.thisptr.optimizer.FunctionMinimizer;
import jp.thisptr.optimizer.gradient.LimitedMemoryBFGS;
import jp.thisptr.optimizer.linesearch.BacktrackingLineSearcher;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * References:
 * <p><b>[Fletcher 77]</b> R. Fletcher and T. L. Freeman. A Modified Newton Method for Minimization.
 *    Journal of optimization theory and applications Vol.23 No.3. 1977.</p>
 * <p><b>[Rosenbrock 60]</b> H.H. Rosenbrock. An automatic method for finding the greatest or least value
 *    of a function. Comput. J., 3:175, 1960.</p>
 * 
 * @author eiichi
 *
 */
public class LimitedMemoryBFGSTest {
	private static Logger log = LoggerFactory.getLogger(LimitedMemoryBFGSTest.class);
	
	private void runOptimization(final String name, final FunctionMinimizer optimizer, final Function target, final double[] xmin, final int maxIterations, final double epsilon, final double assertEpsilon) {
		log.info(String.format("%s: Start", name));
		
		double[] x = optimizer.minimize(maxIterations, epsilon);
		log.info(String.format("%s: Converged", name));
		
		assertArrayEquals(xmin, x, assertEpsilon);
	}

	private void runOptimization(final String name, final FunctionMinimizer optimizer, final Function target, final double[] xmin, final int maxIterations, final double epsilon) {
		runOptimization(name, optimizer, target, xmin, maxIterations, epsilon, 1e-3);
	}

	@Test
	public void testSimpleLibBFGSSample() {
		final int n = 100;

		final double[] xmin = new double[n];
		for (int i = 0; i < n; ++i)
			xmin[i] = 1.0;

		final double[] x0 = new double[n];
		for (int i = 0; i < n; i += 2) {
			x0[i] = -1.2;
			x0[i + 1] = 1.0;
		}

		final Function target = new Function() {
			@Override
			public double f(final double[] x) {
				double result = 0.0;
				for (int i = 0; i < n; i += 2) {
					double t1 = 1.0 - x[i];
					double t2 = 10.0 * (x[i + 1] - x[i] * x[i]);
					result += t1 * t1 + t2 * t2;
				}
				return result;
			}
			@Override
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
			@Override
			public int xdim() {
				return n;
			}
		};

		final FunctionMinimizer optimizer = new LimitedMemoryBFGS(target, x0, new BacktrackingLineSearcher());
		runOptimization("LibBFGSSample", optimizer, target, xmin, 100, 1e-5);
	}

	@Test
	public void testSimpleEllipticParaboloid() {
		final int n = 100;
		
		final double[] xmin = new double[n];
		final UniformDistribution distribution = new UniformDistribution(-100, 100);
		for (int i = 0; i < 100; ++i)
			xmin[i] = distribution.sample();

		final Function target = new Function() {
			public double f(final double[] x) {
				// y = (x[0] - e[1])^2 + (x[1] - e[1])^2 + ... + (x[n] - e[n])^2
				double result = 0.0;
				for (int i = 0; i < n; ++i)
					result += Math.pow(x[i] - xmin[i], 2);
				return result;
			}
			public double[] df(final double[] x) {
				// y' = [2 * (x[0] - e[1]), 2 * (x[1] - e[1]), ..., 2 * (x[n] - e[n])]
				final double[] result = new double[n];
				for (int i = 0; i < n; ++i)
					result[i] = 2 * (x[i] - xmin[i]);
				return result;
			}
			public int xdim() {
				return n;
			}
		};

		final FunctionMinimizer optimizer = new LimitedMemoryBFGS(target);
		runOptimization("Paraboloid", optimizer, target, xmin, 10, 1e-5);
	}
	
	/**
	 * Powell's singular function of four variables [Fletcher 77].
	 */
	@Test
	public void testPowells() {
		final Function target = new Function() {
			@Override
			public int xdim() {
				return 4;
			}
			@Override
			public double f(final double[] x) {
				return Math.pow(x[0] + 10 * x[1], 2) + 5 * Math.pow(x[2] - x[3], 2) + Math.pow(x[1] - 2 * x[2], 4) + 10 * Math.pow(x[0] - x[3], 4);
			}
			@Override
			public double[] df(final double[] x) {
				return new double[] {
						2 * (x[0] + 10 * x[1]) + 40 * Math.pow(x[0] - x[3], 3),
						20 * (x[0] + 10 * x[1]) + 4 * Math.pow(x[1] - 2 * x[2], 3),
						10 * (x[2] - x[3]) - 8 * (x[1] - 2 * x[2]),
						-10 * (x[2] - x[3]) - 40 * Math.pow(x[0] - x[3], 3)
				};
			}
		};
		
		final double[] x0 = new double[] { 3, -1, 0, 1 };
		assertEquals(215, target.f(x0), 0.01);
		
		final double[] xmin = new double[] { 0, 0, 0, 0 };
		assertEquals(0, target.f(xmin), 0.01);
		
		final LimitedMemoryBFGS minimizer = new LimitedMemoryBFGS(target, x0);
		runOptimization("Powells", minimizer, target, xmin, 10000, 1e-7, 1e-2);
	}
	
	/**
	 * Rosenbrock's curved valley.
	 */
	@Test
	public void testRosenbrocks() {
		final Function target = new Function() {
			@Override
			public int xdim() {
				return 2;
			}
			@Override
			public double f(final double[] x) {
				return 100 * Math.pow(x[1] - x[0] * x[0], 2) + Math.pow(1 - x[0], 2);
			}
			@Override
			public double[] df(final double[] x) {
				return new double[] {
						-400 * (x[1] - x[0] * x[0]) * x[0] - 2 * (1 - x[0]),
						200 * (x[1] - x[0] * x[0])
				};
			}
		};
		
		final double[] x0 = new double[] { -1.2, 1.0 };
		assertEquals(24.20, target.f(x0), 0.01);
		
		final double[] xmin = new double[] { 1.0, 1.0 };
		assertEquals(0.0, target.f(xmin), 0.01);
		
		final LimitedMemoryBFGS minimizer = new LimitedMemoryBFGS(target, x0);
		runOptimization("Rosenbrocks", minimizer, target, xmin, 100, 1e-5);
	}

	/**
	 * Wood's function in four parameters [Fletcher 77].
	 */
	// @Test
	public void testWoods() {
		final Function target = new Function() {
			@Override
			public int xdim() {
				return 4;
			}
			@Override
			public double f(final double[] v) {
				final double w = v[0];
				final double x = v[1];
				final double y = v[2];
				final double z = v[3];
				return 100 * Math.pow(x - w * w, 2)
						+ Math.pow(w - 1, 2)
						+ 90 * Math.pow(z - y * y, 2)
						+ Math.pow(1 - y, 2)
						+ 10.1 * Math.pow(x - 1, 2)
						+ 10.1 * Math.pow(z - 1, 2)
						+ 19.8 * (x - 1) * (z - 1);
			}
			@Override
			public double[] df(final double[] v) {
				final double w = v[0];
				final double x = v[1];
				final double y = v[2];
				final double z = v[3];
				return new double[] {
						-2 * 2 * 100 * w * (x - w * w) + 2 * (w - 1),
						100 * 2 * (x - w * w) + 10.1 * 2 * (x - 1) + 19.8 * (z - 1),
						90 * 2 * (x - y * y) * (-2 * y) - 2 * (1 - y),
						90 * 2 * (z - y * y) + 10.1 * 2 * (z - 1) + 19.8 * (x - 1)
				};
			}
		};

		final double[] x0 = new double[] { -3.0, -1.0, -3.0, -1.0 };
		assertEquals(19192, target.f(x0), 0.0001);
		
		final double[] xmin = new double[] { 1.0, 1.0, 1.0, 1.0 };
		assertEquals(0, target.f(xmin), 0.0001);

		final FunctionMinimizer optimizer = new LimitedMemoryBFGS(target, x0, new BacktrackingLineSearcher(0.5, 2.1, 0.0, 0.99, 1000), 100);
		runOptimization("Woods", optimizer, target, xmin, 1000, 1e-5);
	}

	// @Test
	public void testFletcherAndPowellHelicalValley() {
		final Function target = new Function() {
			@Override
			public int xdim() {
				return 3;
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
			@Override
			public double f(final double[] w) {
				final double x = w[0];
				final double y = w[1];
				final double z = w[2];
				return 100 * (Math.pow(z - 10 * phi(x, y), 2) + Math.pow(Math.sqrt(x * x + y * y) - 1, 2)) + z * z;
			}
			@Override
			public double[] df(final double[] w) {
				final double x = w[0];
				final double y = w[1];
				final double z = w[2];
				return new double[] {
						100 * ((2 * z - 20 * phi(x, y)) * dphidx(x, y) + 2 * x - 2 * x / Math.sqrt(x * x + y * y)),
						100 * ((2 * z - 20 * phi(x, y)) * dphidy(x, y) + 2 * y - 2 * y / Math.sqrt(x * x + y * y)),
						100 * (2 * z - 20 * phi(x, y)) + 2 * z
				};
			}
		};

		final double[] x0 = new double[] { -1.0, 0.0, 0.0 };
		assertEquals(2500, target.f(x0), 0.0001);
		
		final double[] xmin = new double[] { 1.0, 0.0, 0.0 };
		assertEquals(0, target.f(xmin), 0.0001);

		final FunctionMinimizer optimizer = new LimitedMemoryBFGS(target, x0, new BacktrackingLineSearcher(0.5, 2.1, 0.0001, Double.POSITIVE_INFINITY, 1000));
		runOptimization("HelicalValley", optimizer, target, xmin, 1000, 1e-5);
	}

//	private static class Instance {
//		public final double y;
//		public final Vector x;
//		public Instance(final Vector x, final double y) {
//			this.x = x;
//			this.y = y;
//		}
//	}

//	@Test
//	public void testL2RegularizedLogLinearModel() {
//		final double[] expected = new double[] { 0.1, 0.40, -0.07, 0.05, -0.20 };
////		final double[] expected = new double[] { 0.0, 0.40 };
//		final Instance[] instances = new Instance[] {
//				// new Instance(new DenseArrayVector(intercept, good, bad, exciting, boring), label)
//				new Instance(new DenseArrayVector(1, 1, 1, 0, 0), 0.99999999999),
//				new Instance(new DenseArrayVector(1, 0, 0, 1, 0), 0.99999999999),
//				new Instance(new DenseArrayVector(1, 0, 1, 0, 1), 0.00000000001),
//				new Instance(new DenseArrayVector(1, 0, 1, 1, 0), 0.00000000001),
//		};
//
//		final Function target = FunctionUtils.negate(new Function() {
//			public double p(final Vector w, final Vector x) {
//				double dot = 0.0;
//				for (int i = 0; i < w.size(); ++i)
//					dot += w.get(i) * x.get(i);
//				return dot / (dot + 1);
//			}
//			@Override
//			public double f(final Vector w) {
//				double result = 0.0;
//				for (final Instance instance : instances)
//					result += (instance.y) * Math.log(p(w, instance.x)) + (1 - instance.y) * Math.log(1 - p(w, instance.x));
//				return result; // TODO: add regularization term
//			}
//			@Override
//			public Vector df(final Vector w) {
//				final double[] result = new double[expected.length];
//				for (final Instance instance : instances)
//					ArrayMath.addScaled(result, instance.y - p(w, instance.x), ((DenseArrayVector) instance.x).rawArray());
//				return DenseArrayVector.wrap(result); // TODO: add regularization term
//			}
//			@Override
//			public int xdim() {
//				return expected.length;
//			}
//		});
//
//		final FunctionMinimizer optimizer = new LimitedMemoryBFGS(target, new DenseArrayVector(expected.length), new BacktrackingLineSearcher());
//		runOptimization("L2LogLinear", optimizer, target, expected, 200);
//	}
}
