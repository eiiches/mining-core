package jp.thisptr.math.vector;

import java.util.Iterator;

import jp.thisptr.math.vector.Vector.Element;

public interface Vector extends Iterable<Element> {
	int size();
	int capacity();
	
	/**
	 * Get value at the given index.
	 * @param index
	 * @return
	 */
	double get(final int index);
	
	/**
	 * Set value at the given index.
	 * @param index
	 * @param value
	 * @throws UnsupportedOperationException if this vector does not support modification.
	 */
	void set(final int index, final double value);
	
	/**
	 * Visit each element in this vector. Note that the order of visits is not specified.
	 * @param visitor
	 */
	void walk(final Visitor visitor);

	public interface Visitor {
		void visit(final int index, final double value);
	}
	
	public interface Element {
		int index();
		double value();
	}
	
	Iterator<Element> iterator();
}