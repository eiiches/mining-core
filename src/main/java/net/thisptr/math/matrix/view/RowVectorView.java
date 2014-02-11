package net.thisptr.math.matrix.view;

import net.thisptr.math.matrix.Matrix;
import net.thisptr.math.vector.AbstractVector;
import net.thisptr.math.vector.Vector;
import net.thisptr.math.vector.VectorShape;
import net.thisptr.math.vector.VectorVisitor;

public class RowVectorView extends AbstractVector {
	private Matrix buf;
	private int row;
	private VectorShape shape;

	public RowVectorView(final Matrix m, final int row) {
		this(m, row, VectorShape.Row);
	}

	private RowVectorView(final Matrix m, final int row, final VectorShape as) {
		this.buf = m;
		this.row = row;
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
		return new RowVectorView(buf, row, shape.transpose());
	}

	@Override
	public int size() {
		return buf.columns();
	}

	@Override
	public int capacity() {
		return buf.columns();
	}

	@Override
	public void resize(int size) {
		throw new UnsupportedOperationException();
	}

	@Override
	public VectorShape shape() {
		return shape;
	}

	@Override
	public double get(int index) {
		return buf.get(row, index);
	}

	@Override
	public void set(int index, double value) {
		buf.set(row, index, value);
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
