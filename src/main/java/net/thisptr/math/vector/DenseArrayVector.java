package net.thisptr.math.vector;

import java.util.Arrays;

public class DenseArrayVector extends DenseVector {
	private double[] array;

	public DenseArrayVector(final Vector src) {
		final int size = src.size();
		array = new double[size];
		src.walk(new VectorVisitor() {
			@Override
			public void visit(int index, double value) {
				array[index] = value;
			}
		});
	}

	public DenseArrayVector(final int size) {
		array = new double[size];
	}

	public DenseArrayVector(final double[] values) {
		array = Arrays.copyOf(values, values.length);
	}

	private DenseArrayVector(final double[] v, final boolean dummy) {
		array = v;
	}

	public DenseArrayVector(boolean[] x0) {
		array = new double[x0.length];
		for (int i = 0; i < x0.length; ++i)
			if (x0[i])
				array[i] = 1;
	}

	public double[] raw() {
		return array;
	}

	public static DenseArrayVector wrap(final double[] v) {
		return new DenseArrayVector(v, true);
	}

	@Override
	public double get(final int index) {
		return array[index];
	}

	@Override
	public void set(final int index, final double value) {
		array[index] = value;
	}

	@Override
	public int size() {
		return array.length;
	}

	@Override
	public void resize(final int size) {
		final double[] newarray = new double[size];
		System.arraycopy(this.array, 0, newarray, 0, Math.min(this.array.length, size));
		this.array = newarray;
	}

	@Override
	public int capacity() {
		return array.length;
	}

	@Override
	public double walk(final VectorVisitor visitor) {
		for (int i = 0; i < array.length; ++i)
			if (array[i] != 0.0)
				visitor.visit(i, array[i]);
		return visitor.finish();
	}
}