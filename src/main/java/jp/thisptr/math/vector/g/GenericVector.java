package jp.thisptr.math.vector.g;

public abstract class GenericVector<T> {
	public abstract T get(final int index);
	public abstract void set(final int index, final T value);
}
