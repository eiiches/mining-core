package net.thisptr.math.vector;

import net.thisptr.lang.NotImplementedException;

public abstract class AbstractVector implements Vector {
	private void validateAccess(final int row, final int column) {
		if (column != 0)
			throw new IndexOutOfBoundsException();
	}

	@Override
	public Vector column(int column) {
		validateAccess(0, column);

		return this;
	}

	@Override
	public Vector row(int row) {
		throw new NotImplementedException();
	}

	@Override
	public double get(int row, int column) {
		validateAccess(row, column);

		return get(row);
	}

	@Override
	public void set(int row, int column, double value) {
		validateAccess(row, column);

		set(row, value);
	}

	@Override
	public void resize(int rows, int columns) {
		if (columns != 1)
			throw new IllegalArgumentException("Vector cannot extend columns");
		resize(rows);
	}

	@Override
	public Vector transpose() {
		throw new NotImplementedException();
	}

	@Override
	public int columns() {
		return 1;
	}

	@Override
	public int rows() {
		return size();
	}

	@Override
	public void walk(final MatrixVisitor visitor) {
		walk(new VectorVisitor() {
			@Override
			public void visit(int index, double value) {
				visitor.visit(index, 0, value);
			}
		});
	}
}
