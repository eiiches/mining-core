package net.thisptr.math.matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

import net.thisptr.lang.NotImplementedException;
import net.thisptr.math.matrix.view.ColumnVectorView;
import net.thisptr.math.matrix.view.RowVectorView;
import net.thisptr.math.vector.DenseByteBufferVector;
import net.thisptr.math.vector.Vector;
import net.thisptr.math.vector.VectorShape;

public class DenseByteBufferMatrix implements Matrix {
	private static final int DOUBLE_BYTES = Double.SIZE / 8;

	protected ByteBuffer buf;
	protected DoubleBuffer dbuf;
	private int[] index;

	private StorageOrder order;

	private int rows;
	private int columns;

	public DenseByteBufferMatrix(final int rows, final int columns) {
		this(rows, columns, StorageOrder.RowMajor);
	}

	public DenseByteBufferMatrix(final int rows, final int columns, final StorageOrder order) {
		this(rows, columns, order, ByteBuffer.allocateDirect(rows * columns * DOUBLE_BYTES).order(ByteOrder.nativeOrder()));
	}

	public DenseByteBufferMatrix(final int rows, final int columns, final double[][] initializer) {
		this(rows, columns);
		fill(initializer);
	}

	public DenseByteBufferMatrix(final int rows, final int columns, final StorageOrder order, final double[][] initializer) {
		this(rows, columns, order);
		fill(initializer);
	}

	public DenseByteBufferMatrix(final Matrix m) {
		this(m.rows(), m.columns(), StorageOrder.RowMajor);
		m.walk(new MatrixVisitor() {
			@Override
			public void visit(int row, int column, double value) {
				set(row, column, value);
			}
		});
	}

	protected DenseByteBufferMatrix(final int rows, final int columns, final StorageOrder order, final ByteBuffer buf) {
		this.rows = rows;
		this.columns = columns;
		this.buf = buf.duplicate().order(buf.order());
		this.dbuf = this.buf.asDoubleBuffer();
		this.order = order;
		buildIndex();
	}

	public static DenseByteBufferMatrix wrap(final int rows, final int columns, final StorageOrder order, final ByteBuffer buf) {
		return new DenseByteBufferMatrix(rows, columns, order, buf);
	}

	@Override
	public Matrix transpose() {
		return wrap(columns, rows, order.transpose(), buf);
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
	public double get(final int row, final int column) {
		switch (order) {
			case ColumnMajor:
				return dbuf.get(index[column] + row);
			case RowMajor:
				return dbuf.get(index[row] + column);
		}
		throw new IllegalStateException();
	}

	@Override
	public void set(int row, int column, double value) {
		switch (order) {
			case ColumnMajor:
				dbuf.put(index[column] + row, value);
				return;
			case RowMajor:
				dbuf.put(index[row] + column, value);
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
				final ByteBuffer _buf = buf.duplicate().order(buf.order());
				_buf.position(index[row] * DOUBLE_BYTES);
				_buf.limit((index[row] + columns) * DOUBLE_BYTES);
				return DenseByteBufferVector.wrap(columns, VectorShape.Row, _buf);
		}
		throw new IllegalStateException();
	}

	@Override
	public Vector column(final int column) {
		switch (order) {
			case ColumnMajor:
				final ByteBuffer _buf = buf.duplicate().order(buf.order());
				_buf.position(index[column] * DOUBLE_BYTES);
				_buf.limit((index[column] + rows) * DOUBLE_BYTES);
				return DenseByteBufferVector.wrap(rows, VectorShape.Column, _buf);
			case RowMajor:
				return new ColumnVectorView(this, column);
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void resize(int rows, int columns) {
		throw new NotImplementedException();
	}

	@Override
	public double walk(MatrixVisitor visitor) {
		for (int i = 0; i < rows; ++i)
			for (int j = 0; j < columns; ++j) {
				final double value = get(i, j);
				if (value != 0.0)
					visitor.visit(i, j, value);
			}
		return visitor.finish();
	}

	public ByteBuffer raw() {
		return buf;
	}

	public StorageOrder storageOrder() {
		return order;
	}

	private void buildIndex() {
		switch (order) {
			case ColumnMajor: {
				final int[] index = new int[columns];
				for (int j = 0; j < columns; ++j)
					index[j] = j * rows;
				this.index = index;
				return;
			}
			case RowMajor: {
				final int[] index = new int[rows];
				for (int i = 0; i < rows; ++i)
					index[i] = i * columns;
				this.index = index;
				return;
			}
		}
		throw new IllegalStateException();
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
