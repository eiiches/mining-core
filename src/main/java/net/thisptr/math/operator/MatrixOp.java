package net.thisptr.math.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import net.thisptr.util.Range;
import net.thisptr.util.ThreadUtils;

public class MatrixOp {
	/**
	 * Run a matrix multiplication: r = x * y
	 * 
	 * @param r
	 * @param x
	 * @param y
	 * @param executor
	 * @param hintNumThreads
	 */
	public static void assign_X_Y(final double[][] r, final double[][] x, final double[][] y, final ExecutorService executor, final int hintNumThreads) {
		final int rows = x.length;
		final int cols = y[0].length; // FIXME: handle case, y.length == 0.
		final int dim = y.length;

		final List<Runnable> tasks = new ArrayList<>();
		for (final Range rowsplit : new Range(0, rows).split(hintNumThreads)) {
			for (final Range colsplit : new Range(0, cols).split(hintNumThreads)) {
				tasks.add(new Runnable() {
					@Override
					public void run() {
						final double[][] _x = x;
						final double[][] _y = y;
						final double[][] _r = r;
						final int _dim = dim;
						final int rowEnd = rowsplit.end();
						final int colEnd = colsplit.end();
						for (int row = rowsplit.begin(); row < rowEnd; ++row) {
							for (int col = colsplit.begin(); col < colEnd; ++col) {
								double tmp = 0.0;
								for (int k = 0; k < _dim; ++k)
									tmp += _x[row][k] * _y[k][col];
								_r[row][col] = tmp;
							}
						}
					}
				});
			}
		}

		try {
			ThreadUtils.invokeAll(tasks, executor);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Run a matrix multiplication: r = x * y
	 * 
	 * @param r
	 * @param x
	 * @param y
	 * @param executor
	 * @param hintNumThreads
	 */
	public static void assign_X_Y(final double[][] r, final boolean[][] x, final double[][] y, final ExecutorService executor, final int hintNumThreads) {
		final int rows = x.length;
		final int cols = y[0].length; // FIXME: handle case, y.length == 0.
		final int dim = y.length;

		final List<Runnable> tasks = new ArrayList<>();
		for (final Range rowsplit : new Range(0, rows).split(hintNumThreads)) {
			for (final Range colsplit : new Range(0, cols).split(hintNumThreads)) {
				tasks.add(new Runnable() {
					@Override
					public void run() {
						final boolean[][] _x = x;
						final double[][] _y = y;
						final double[][] _r = r;
						final int _dim = dim;
						final int rowEnd = rowsplit.end();
						final int colEnd = colsplit.end();
						for (int row = rowsplit.begin(); row < rowEnd; ++row) {
							for (int col = colsplit.begin(); col < colEnd; ++col) {
								double tmp = 0.0;
								for (int k = 0; k < _dim; ++k)
									if (_x[row][k])
										tmp += _y[k][col];
								_r[row][col] = tmp;
							}
						}
					}
				});
			}
		}

		try {
			ThreadUtils.invokeAll(tasks, executor);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Run a matrix multiplication: r = x * y^T
	 * <p>
	 * This method runs 10~20 times faster than
	 * {@link MatrixOp#assign_X_Y(double[][], double[][], double[][], ExecutorService, int)}
	 * , since matrices are stored row-major order in memory.
	 * 
	 * @param r
	 * @param x
	 * @param y
	 * @param executor
	 * @param hintNumThreads
	 */
	public static void assign_X_Yt(final double[][] r, final double[][] x, final double[][] y, final ExecutorService executor, final int hintNumThreads) {
		final int rows = x.length;
		final int cols = y.length;
		final int dim = y[0].length; // FIXME: handle case, y.length == 0.

		final List<Runnable> tasks = new ArrayList<>();
		for (final Range rowsplit : new Range(0, rows).split(hintNumThreads)) {
			for (final Range colsplit : new Range(0, cols).split(hintNumThreads)) {
				tasks.add(new Runnable() {
					@Override
					public void run() {
						final double[][] _x = x;
						final double[][] _y = y;
						final double[][] _r = r;
						final int _dim = dim;
						for (int row = rowsplit.begin(); row < rowsplit.end(); ++row) {
							for (int col = colsplit.begin(); col < colsplit.end(); ++col) {
								double tmp = 0.0;
								for (int k = 0; k < _dim; ++k)
									tmp += _x[row][k] * _y[col][k];
								_r[row][col] = tmp;
							}
						}
					}
				});
			}
		}

		try {
			ThreadUtils.invokeAll(tasks, executor);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Run a matrix multiplication: r = x * y^T
	 * 
	 * @param r
	 * @param x
	 * @param y
	 * @param executor
	 * @param hintNumThreads
	 */
	public static void assign_X_Yt(final double[][] r, final boolean[][] x, final double[][] y, final ExecutorService executor, final int hintNumThreads) {
		final int rows = x.length;
		final int cols = y.length;
		final int dim = y[0].length; // FIXME: handle case, y.length == 0.

		final List<Runnable> tasks = new ArrayList<>();
		for (final Range rowsplit : new Range(0, rows).split(hintNumThreads)) {
			for (final Range colsplit : new Range(0, cols).split(hintNumThreads)) {
				tasks.add(new Runnable() {
					@Override
					public void run() {
						for (int row = rowsplit.begin(); row < rowsplit.end(); ++row) {
							for (int col = colsplit.begin(); col < colsplit.end(); ++col) {
								double tmp = 0.0;
								for (int k = 0; k < dim; ++k)
									if (x[row][k])
										tmp += y[col][k];
								r[row][col] = tmp;
							}
						}
					}
				});
			}
		}

		try {
			ThreadUtils.invokeAll(tasks, executor);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}