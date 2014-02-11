package net.thisptr.math.matrix.view;

import net.thisptr.math.matrix.Matrix;
import net.thisptr.math.vector.AbstractVector;
import net.thisptr.math.vector.Vector;
import net.thisptr.math.vector.VectorShape;
import net.thisptr.math.vector.VectorVisitor;

public class ColumnVectorView extends AbstractVector {
	private Matrix buf;
	private int column;
	private VectorShape shape;

	public ColumnVectorView(final Matrix m, final int column) {
		this(m, column, VectorShape.Column);
	}

	private ColumnVectorView(final Matrix m, final int column, final VectorShape as) {
		this.buf = m;
		this.column = column;
		this.shape = as;
	}

	@Override
	public Vector row(int row) {
		return new RowVectorView(this, row);
	}

	@Override
	public Vector column(int column) {
		return new ColumnVectorView(this, column);
	}

	@Override
	public Vector transpose() {
		return new ColumnVectorView(buf, column, shape.transpose());
	}

	@Override
	public int size() {
		return buf.rows();
	}

	@Override
	public int capacity() {
		return buf.rows();
	}

	@Override
	public void resize(int size) {
		throw new UnsupportedOperationException();
	}

	@Override
	public VectorShape shape() {
		return VectorShape.Column;
	}

	@Override
	public double get(int index) {
		return buf.get(index, column);
	}

	@Override
	public void set(int index, double value) {
		buf.set(index, column, value);
	}

	@Override
	public double walk(VectorVisitor visitor) {
		final int size = size();
		for (int i = 0; i < size; ++i) {
			final double value = get(i);
			if (value == 0.0)
				continue;
			visitor.visit(i, value);
		}
		return visitor.finish();
	}
}
