package net.thisptr.math.matrix;

import java.util.Iterator;

import net.thisptr.math.matrix.Matrix.Element;

public interface Matrix extends Iterable<Element> {
	int rowSize();
	int rowCapacity();
	
	int colSize();
	int colCapacity();
	
	
	/**
	 * Get value at the given position.
	 * @param row
	 * @param col
	 * @return
	 */
	double get(final int row, final int col);
	
	/**
	 * Set value at the given position.
	 * @param row
	 * @param col
	 * @param value
	 */
	void set(final int row, final int col, final double value);
	
	public interface Visitor {
		void visit(final int rowIndex, final int colIndex, final double value);
	}
	
	void walk(final Visitor visitor);
	
	public interface Element {
		int rowIndex();
		int colIndex();
		double value();
	}
	
	Iterator<Element> iterator();
}
