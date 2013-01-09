package jp.thisptr.math.structure.matrix;

import java.util.HashMap;
import java.util.Map;

import jp.thisptr.math.structure.vector.SparseMapVector;

public class SparseMapMatrix extends SparseMatrix {
	private final Map<Integer, SparseMapVector> data = new HashMap<Integer, SparseMapVector>();
	
	@Override
	public double get(final int row, final int col) {
		final SparseMapVector rowVector = data.get(row);
		if (rowVector == null)
			return 0.0;
		return rowVector.get(col);
	}

	@Override
	public void set(final int row, final int col, final double value) {
		SparseMapVector rowVector = data.get(row);
		if (rowVector == null) {
			rowVector = new SparseMapVector();
			data.put(row, rowVector);
		}
		rowVector.set(col, value);
	}
}
