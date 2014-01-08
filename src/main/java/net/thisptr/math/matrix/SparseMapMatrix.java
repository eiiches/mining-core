package net.thisptr.math.matrix;

import java.util.HashMap;
import java.util.Map;

import net.thisptr.lang.NotImplementedException;
import net.thisptr.math.vector.SparseMapVector;
import net.thisptr.math.vector.Vector;

public class SparseMapMatrix extends SparseMatrix {
	private Map<Integer, SparseMapVector> data = new HashMap<Integer, SparseMapVector>();
	private int rows;
	private int columns;

	public SparseMapMatrix(final int rows, final int cols) {
		this.rows = rows;
		this.columns = cols;
	}

	@Override
	public double get(final int row, final int col) {
		// FIXME: check bounds
		final SparseMapVector rowVector = data.get(row);
		if (rowVector == null)
			return 0.0;
		return rowVector.get(col);
	}

	@Override
	public void set(final int row, final int col, final double value) {
		// FIXME: check bounds
		SparseMapVector rowVector = data.get(row);
		if (rowVector == null) {
			rowVector = new SparseMapVector(col);
			data.put(row, rowVector);
		}
		rowVector.set(col, value);
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
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public void resize(int rows, int columns) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public Vector column(int column) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public Vector row(int row) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public void walk(final MatrixVisitor visitor) {
		for (final Map.Entry<Integer, SparseMapVector> row : data.entrySet()) {
			row.getValue().walk(new Vector.VectorVisitor() {
				@Override
				public void visit(final int index, final double value) {
					visitor.visit(row.getKey(), index, value);
				}
			});
		}
	}
}
