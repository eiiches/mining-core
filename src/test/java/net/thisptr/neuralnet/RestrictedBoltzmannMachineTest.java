package net.thisptr.neuralnet;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.thisptr.math.vector.DenseArrayVector;
import net.thisptr.math.vector.Vector;
import net.thisptr.math.vector.formatter.DefaultVectorFormatter;
import net.thisptr.neuralnet.RestrictedBoltzmannMachine.UnitType;

import org.junit.Test;

public class RestrictedBoltzmannMachineTest {
	public static Vector v(final double[] x) {
		return new DenseArrayVector(x);
	}

	final List<Vector> inputVectors = Arrays.asList(new Vector[] {
			v(new double[] { 1, 1, 1, 0, 0, 0 }),
			v(new double[] { 1 ,0, 1, 0, 0, 0 }),
			v(new double[] { 1, 1, 1, 0, 0, 0 }),
			v(new double[] { 0, 0, 1, 1, 1, 0 }),
			v(new double[] { 0, 0, 1, 1, 0, 0 }),
			v(new double[] { 0, 0, 1, 1, 1, 0 }),
	});
	final Vector testVector = v(new double[] { 0, 0, 0, 1, 1, 0 });

	public double error(final Vector v1, final Vector v2, final int dim) {
		double sum2 = 0.0;
		for (int i = 0; i < dim; ++i)
			sum2 += Math.pow(v2.get(i) - v1.get(i), 2);
		return Math.sqrt(sum2);
	}

	public static Random random = new Random();

	public static double logistic(final double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}

	@Test
	public void testFastLogistic() {
		assertEquals(FastLogisticFunction.logistic(-10), logistic(-10), 0.01);
		assertEquals(FastLogisticFunction.logistic(-1), logistic(-1), 0.01);
		assertEquals(FastLogisticFunction.logistic(0), logistic(0), 0.01);
		assertEquals(FastLogisticFunction.logistic(1), logistic(1), 0.01);
		assertEquals(FastLogisticFunction.logistic(2), logistic(2), 0.01);
	}

	@Test
	public void test() {
		final long start = System.currentTimeMillis();

		final int DIM_L0 = 6;
		final int DIM_L1 = 4;
		final int DIM_L2 = 2;
		final int DIM_L3 = 2;

		final int MAX_ITERATIONS = 5000;

		final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try {
			// train
			final RestrictedBoltzmannMachine sut1 = new RestrictedBoltzmannMachine(DIM_L0, DIM_L1, 0.03, 0.5);
			for (int i = 0; i < MAX_ITERATIONS; ++i)
				for (final Vector inputVector : inputVectors)
					sut1.train(inputVector);

			final RestrictedBoltzmannMachine sut2 = new RestrictedBoltzmannMachine(DIM_L1, DIM_L2, 0.03, 0.5);
			for (int i = 0; i < MAX_ITERATIONS; ++i)
				for (final Vector in : inputVectors)
					sut2.train(sut1.reduce(in));

			final RestrictedBoltzmannMachine sut3 = new RestrictedBoltzmannMachine(DIM_L2, DIM_L3, 0.03, 0.5);
			sut3.setHiddenUnitType(UnitType.Linear);
			for (int i = 0; i < MAX_ITERATIONS; ++i)
				for (final Vector in : inputVectors)
					sut3.train(sut2.reduce(sut1.reduce(in)));

			// weights
//			for (final double[] arra : sut1.weights())
//				System.out.println(DenseArrayVector.wrap(arra));
//			System.out.println();
//
//			for (final double[] arra : sut2.weights())
//				System.out.println(DenseArrayVector.wrap(arra));
//			System.out.println();
//
//			for (final double[] arra : sut3.weights())
//				System.out.println(DenseArrayVector.wrap(arra));
//			System.out.println();

			final List<Vector> ins = new ArrayList<>();
			ins.addAll(inputVectors);
			ins.add(testVector);

			final DefaultVectorFormatter formatter = new DefaultVectorFormatter();
			formatter.setPrecision(10);
			formatter.setSparseOutput(false);
			formatter.setColorRange(0, 1);

			final DefaultVectorFormatter bolder = new DefaultVectorFormatter();
			bolder.setBold(true);
			bolder.setPrecision(10);
			bolder.setSparseOutput(false);
			bolder.setColorRange(0, 1);

			// out
			for (final Vector r0 : ins) {
				final Vector r1 = sut1.reduce(r0);
				final Vector r2 = sut2.reduce(r1);
				final Vector r3 = sut3.reduce(r2);
				final Vector e2 = sut3.reconstruct(r3);
				final Vector e1 = sut2.reconstruct(e2);
				final Vector e0 = sut1.reconstruct(e1);
				System.out.printf("in: %s  ---> %s ---> %s ---> %s (errors = %.2f, %.2f)%n", bolder.format(r0), formatter.format(r1), formatter.format(r2), formatter.format(r3), error(e0, r0, DIM_L0), error(r1, e1, DIM_L1));
				System.out.printf("    %s <--- %s <--- %s <----------┛ %n", formatter.format(e0), formatter.format(e1), formatter.format(e2));
			}

			final long end = System.currentTimeMillis();
			System.out.printf("Time took: %.3f sec.%n", (end - start) / 1000.0);
		} finally {
			executor.shutdown();
		}
	}
}
