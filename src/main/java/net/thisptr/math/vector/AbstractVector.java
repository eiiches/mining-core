package net.thisptr.math.vector;

import net.thisptr.math.matrix.MatrixVisitor;

public abstract class AbstractVector implements Vector {
	@Override
	public double get(int row, int column) {
		switch (shape()) {
			case Column:
				if (column != 0)
					throw new IndexOutOfBoundsException();
				return get(row);
			case Row:
				if (row != 0)
					throw new IndexOutOfBoundsException();
				return get(column);
		}
		throw new IllegalStateException();
	}

	@Override
	public void set(int row, int column, double value) {
		switch (shape()) {
			case Column:
				if (column != 0)
					throw new IndexOutOfBoundsException();
				set(row, value);
				break;
			case Row:
				if (row != 0)
					throw new IndexOutOfBoundsException();
				set(column, value);
				break;
		}
		throw new IllegalStateException();
	}

	@Override
	public void resize(int rows, int columns) {
		switch (shape()) {
			case Column:
				if (columns != 1)
					throw new IllegalArgumentException();
				resize(rows);
				break;
			case Row:
				if (rows != 1)
					throw new IllegalArgumentException();
				resize(columns);
				break;
		}
		throw new IllegalStateException();
	}

	@Override
	public int columns() {
		switch (shape()) {
			case Column:
				return 1;
			case Row:
				return size();
		}
		throw new IllegalStateException();
	}

	@Override
	public int rows() {
		switch (shape()) {
			case Column:
				return size();
			case Row:
				return 1;
		}
		throw new IllegalStateException();
	}

	@Override
	public void walk(final MatrixVisitor visitor) {
		switch (shape()) {
			case Column:
				walk(new VectorVisitor() {
					@Override
					public void visit(int index, double value) {
						visitor.visit(index, 0, value);
					}
				});
				break;
			case Row:
				walk(new VectorVisitor() {
					@Override
					public void visit(int index, double value) {
						visitor.visit(0, index, value);
					}
				});
				break;
		}
		throw new IllegalStateException();
	}
}
