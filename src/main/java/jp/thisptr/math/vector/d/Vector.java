package jp.thisptr.math.vector.d;

public abstract class Vector {
	public abstract double get(final int index);
	public abstract void set(final int index, final double value);
	public abstract int dim();
	public abstract int capacity();
}
