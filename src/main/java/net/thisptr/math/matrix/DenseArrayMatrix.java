package net.thisptr.math.matrix;

import net.thisptr.lang.NotImplementedException;
import net.thisptr.math.matrix.view.ColumnVectorView;
import net.thisptr.math.matrix.view.RowVectorView;
import net.thisptr.math.vector.DenseArrayVector;
import net.thisptr.math.vector.Vector;
import net.thisptr.math.vector.VectorShape;

public class DenseArrayMatrix implements Matrix {
	private double[][] buf;
	private StorageOrder order;

	private int rows;
	private int columns;

	public DenseArrayMatrix(final int rows, final int columns) {
		this(rows, columns, StorageOrder.RowMajor);
	}

	public DenseArrayMatrix(final int rows, final int columns, final StorageOrder order) {
		this.rows = rows;
		this.columns = columns;
		this.order = order;

		switch (order) {
			case ColumnMajor:
				this.buf = new double[columns][rows];
				break;
			case RowMajor:
				this.buf = new double[rows][columns];
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	public DenseArrayMatrix(final int rows, final int columns, final double[][] initializer) {
		this(rows, columns);
		fill(initializer);
	}

	public DenseArrayMatrix(final int rows, final int columns, final StorageOrder order, final double[][] initializer) {
		this(rows, columns, order);
		fill(initializer);
	}

	private DenseArrayMatrix(final int rows, final int columns, final StorageOrder order, final double[][] buf, final boolean dummy) {
		this.buf = buf;
		this.rows = rows;
		this.columns = columns;
		this.order = order;
	}

	public DenseArrayMatrix(final Matrix m) {
		this(m.rows(), m.columns());
		for (int i = 0; i < this.rows; ++i)
			for (int j = 0; j < this.columns; ++j)
				this.buf[i][j] = m.get(i, j);
	}

	@Override
	public double get(final int row, final int column) {
		switch (order) {
			case ColumnMajor:
				return buf[column][row];
			case RowMajor:
				return buf[row][column];
		}
		throw new IllegalStateException();
	}

	@Override
	public void set(final int row, final int column, final double value) {
		switch (order) {
			case ColumnMajor:
				buf[column][row] = value;
				return;
			case RowMajor:
				buf[row][column] = value;
				return;
		}
		throw new IllegalStateException();
	}

	@Override
	public Vector row(final int row) {
		switch (order) {
			case ColumnMajor:
				return new RowVectorView(this, row);
			case RowMajor:
				return DenseArrayVector.wrap(buf[row].length, VectorShape.Row, buf[row]);
		}
		throw new IllegalStateException();
	}

	@Override
	public Vector column(final int column) {
		switch (order) {
			case ColumnMajor:
				return DenseArrayVector.wrap(buf[column].length, VectorShape.Column, buf[column]);
			case RowMajor:
				return new ColumnVectorView(this, column);
		}
		throw new IllegalStateException();
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
		return new DenseArrayMatrix(columns, rows, order.transpose(), buf, false);
	}

	@Override
	public void resize(int rows, int columns) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public double walk(final MatrixVisitor visitor) {
		for (int i = 0; i < rows; ++i)
			for (int j = 0; j < columns; ++j) {
				final double value = get(i, j);
				if (value == 0.0)
					continue;
				visitor.visit(i, j, value);
			}
		return visitor.finish();
	}

	public double[][] raw() {
		return buf;
	}

	public StorageOrder storageOrder() {
		return order;
	}

	private void fill(final double[][] initializer) {
		final int irows = Math.min(rows, initializer.length);
		for (int i = 0; i < irows; ++i) {
			final int icolumns = Math.min(columns, initializer[i].length);
			for (int j = 0; j < icolumns; ++j)
				set(i, j, initializer[i][j]);
		}
	}
}