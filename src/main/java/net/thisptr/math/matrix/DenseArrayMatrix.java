package net.thisptr.math.matrix;

import net.thisptr.lang.NotImplementedException;
import net.thisptr.math.vector.DenseArrayVector;
import net.thisptr.math.vector.Vector;

public class DenseArrayMatrix extends DenseMatrix {
	private double[][] data;

	private int rows;
	private int columns;

	private boolean rowMajor = true;

	public DenseArrayMatrix(final int rows, final int columns) {
		this.data = new double[rows][columns];
		this.rows = rows;
		this.columns = columns;
	}

	private DenseArrayMatrix() {
	}

	@Override
	public double get(final int row, final int column) {
		if (rowMajor) {
			return data[row][column];
		} else {
			return data[column][row];
		}
	}

	@Override
	public void set(final int row, final int column, final double value) {
		if (rowMajor) {
			data[row][column] = value;
		} else {
			data[column][row] = value;
		}
	}

	@Override
	public Vector row(final int row) {
		if (rowMajor) {
			return DenseArrayVector.wrap(data[row]);
		} else {
			throw new NotImplementedException();
		}
	}

	@Override
	public Vector column(final int column) {
		if (rowMajor) {
			throw new NotImplementedException();
		} else {
			return DenseArrayVector.wrap(data[column]);
		}
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
	public Matrix transpose() {
		final DenseArrayMatrix result = new DenseArrayMatrix();
		result.data = data;
		result.rows = columns;
		result.columns = rows;
		result.rowMajor = !rowMajor;
		return result;
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

	public double[][] raw() {
		return data;
	}

	public boolean rowMajor() {
		return rowMajor;
	}
}