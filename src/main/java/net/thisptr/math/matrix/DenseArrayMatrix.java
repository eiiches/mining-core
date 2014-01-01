package net.thisptr.math.matrix;

import java.util.Iterator;

import net.thisptr.lang.NotImplementedException;
import net.thisptr.math.vector.DenseArrayVector;
import net.thisptr.math.vector.Vector;

public class DenseArrayMatrix extends DenseMatrix {
	private double[][] data;

	private int rows;
	private int columns;

	public DenseArrayMatrix(final int rows, final int columns) {
		this.data = new double[rows][columns];
		this.rows = rows;
		this.columns = columns;
	}

	@Override
	public double get(final int row, final int column) {
		return data[row][column];
	}

	@Override
	public void set(final int row, final int column, final double value) {
		data[row][column] = value;
	}

	@Override
	public Vector row(final int row) {
		return DenseArrayVector.wrap(data[row]);
	}

	@Override
	public Vector column(final int column) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public int rows() {
		return rows;
	}

	@Override
	public int columns() {
		return columns;
	}

	@Override
	public void resize(int rows, int columns) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public void walk(final Visitor visitor) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public Iterator<Element> iterator() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
}