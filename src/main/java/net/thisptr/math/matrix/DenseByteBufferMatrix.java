package net.thisptr.math.matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.Iterator;

import net.thisptr.lang.NotImplementedException;
import net.thisptr.math.vector.DenseByteBufferVector;
import net.thisptr.math.vector.Vector;

public class DenseByteBufferMatrix extends DenseMatrix {
	private static final int DOUBLE_BYTES = Double.SIZE / 8;

	private ByteBuffer buf;
	private DoubleBuffer dbuf;
	private int[] index;
	private boolean rowMajor = true;

	private int rows;
	private int columns;

	private DenseByteBufferMatrix() {
	}

	public DenseByteBufferMatrix(final int rows, final int columns) {
		this.rows = rows;
		this.columns = columns;
		this.buf = ByteBuffer.allocateDirect(rows * columns * DOUBLE_BYTES).order(ByteOrder.nativeOrder());
		this.dbuf = this.buf.asDoubleBuffer();
		this.index = new int[rows];
		for (int i = 0; i < rows; ++i)
			index[i] = i * columns;
	}

	@Override
	public Matrix transpose() {
		final DenseByteBufferMatrix result = new DenseByteBufferMatrix();
		result.buf = buf;
		result.dbuf = dbuf;
		result.index = index;
		result.rowMajor = !rowMajor;
		result.rows = columns;
		result.columns = rows;
		return result;
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
		if (rowMajor) {
			return dbuf.get(index[row] + column);
		} else {
			return dbuf.get(index[column] + row);
		}
	}

	@Override
	public void set(int row, int column, double value) {
		if (rowMajor) {
			dbuf.put(index[row] + column, value);
		} else {
			dbuf.put(index[column] + row, value);
		}
	}

	@Override
	public Vector row(final int row) {
		if (rowMajor) {
			final ByteBuffer _buf = buf.duplicate().order(buf.order());
			_buf.position(index[row] * DOUBLE_BYTES);
			_buf.limit(columns * DOUBLE_BYTES);
			return new DenseByteBufferVector(_buf);
		} else {
			throw new NotImplementedException();
		}
	}

	@Override
	public Vector column(final int column) {
		if (rowMajor) {
			throw new NotImplementedException();
		} else {
			final ByteBuffer _buf = buf.duplicate().order(buf.order());
			_buf.position(index[column] * DOUBLE_BYTES);
			_buf.limit(rows * DOUBLE_BYTES);
			return new DenseByteBufferVector(_buf);
		}
	}

	@Override
	public void resize(int rows, int columns) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public void walk(Visitor visitor) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public Iterator<Element> iterator() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public ByteBuffer raw() {
		return buf;
	}
	
	public boolean rowMajor() {
		return rowMajor;
	}
}
