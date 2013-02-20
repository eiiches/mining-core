package jp.thisptr.math.vector;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

public interface Vector {
	int size();
	int capacity();
	
	/**
	 * Get value at the given index.
	 * @param index
	 * @return
	 */
	double get(final int index);
	
	/**
	 * Visit each element in this vector. Note that the order of visits is not guaranteed.
	 * @param visitor
	 */
	void accept(final Visitor visitor);
	
	/**
	 * Set value at the given index.
	 * @param index
	 * @param value
	 * @throws UnsupportedOperationException if this vector does not support modification.
	 */
	void set(final int index, final double value);

	public interface Visitor {
		void visit(final int index, final double value);
	}
	
	@Deprecated
	public interface MapAccessible {
		Int2DoubleMap rawMap();
	}
}