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

	private int rows;
	private int columns;

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
	public int rows() {
		return rows;
	}

	@Override
	public int columns() {
		return columns;
	}

	@Override
	public double get(final int row, final int column) {
		return dbuf.get(index[row] + column);
	}

	@Override
	public void set(int row, int column, double value) {
		dbuf.put(index[row] + column, value);
	}

	@Override
	public Vector row(final int row) {
		final ByteBuffer _buf = buf.duplicate();
		_buf.position(index[row] * DOUBLE_BYTES);
		_buf.limit(columns * DOUBLE_BYTES);
		return new DenseByteBufferVector(_buf);
	}

	@Override
	public Vector column(final int column) {
		throw new NotImplementedException();
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
}
