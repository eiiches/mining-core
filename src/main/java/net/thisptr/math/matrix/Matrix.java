package net.thisptr.math.matrix;

import java.util.Iterator;

import net.thisptr.math.matrix.Matrix.Element;
import net.thisptr.math.vector.Vector;

public interface Matrix extends Iterable<Element> {
	int rows();

	int columns();

	/**
	 * Get value at the given position.
	 * 
	 * @param row
	 * @param column
	 * @return value
	 */
	double get(int row, int column);

	/**
	 * Set value at the given position.
	 * 
	 * @param row
	 * @param column
	 * @param value
	 */
	void set(int row, int column, double value);

	/**
	 * Returns the row view of the matrix.
	 * 
	 * @param row
	 * @return Vector
	 */
	Vector row(int row);

	/**
	 * Returns the column view of the matrix.
	 * 
	 * @param column
	 * @return Vector
	 */
	Vector column(int column);

	void resize(int rows, int columns);

	public interface Visitor {
		void visit(int row, int column, double value);
	}

	void walk(final Visitor visitor);

	public interface Element {
		int rowIndex();

		int colIndex();

		double value();
	}

	Iterator<Element> iterator();
}
