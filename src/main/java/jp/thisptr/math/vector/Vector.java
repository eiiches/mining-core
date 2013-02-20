package jp.thisptr.math.vector;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

public interface Vector {
	int size();
	int capacity();
	
	double get(final int index);
	void set(final int index, final double value);
	void accept(final Visitor visitor);

	public interface Visitor {
		void visit(final int index, final double value);
	}
	
	@Deprecated
	public interface MapAccessible {
		Int2DoubleMap rawMap();
	}
}