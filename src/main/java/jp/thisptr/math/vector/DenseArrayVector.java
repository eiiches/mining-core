package jp.thisptr.math.vector;

import java.util.Arrays;

public class DenseArrayVector extends DenseVector {
	private final double[] array;
	
	public DenseArrayVector(final int dimension) {
		array = new double[dimension];
	}
	
	public DenseArrayVector(final double... values) {
		array = Arrays.copyOf(values, values.length);
	}
	
	private DenseArrayVector(final double[] v, final boolean dummy) {
		array = v;
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
	public int capacity() {
		return array.length;
	}

	@Override
	public void accept(final Visitor visitor) {
		for (int i = 0; i < array.length; ++i)
			if (array[i] != 0.0)
				visitor.visit(i, array[i]);
	}
}