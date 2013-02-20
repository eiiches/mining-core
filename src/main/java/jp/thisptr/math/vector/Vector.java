package jp.thisptr.math.vector;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

public abstract class Vector {
	public abstract int size();
	public abstract int capacity();
	public abstract double get(final int index);

	public interface Modifiable {
		void set(final int index, final double value);
	}

	public interface ArrayAccessible {
		double[] rawArray();
	}
	
	public interface MapAccessible {
		Int2DoubleMap rawMap();
	}
}