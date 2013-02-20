package jp.thisptr.math.matrix;

import jp.thisptr.math.vector.DenseArrayVector;

public class DenseArrayMatrix extends DenseMatrix {
	private final DenseArrayVector[] data;
	
	public DenseArrayMatrix(final int nrow, final int ncol) {
		data = new DenseArrayVector[nrow];
		for (int row = 0; row < nrow; ++row)
			data[row] = new DenseArrayVector(ncol);
	}
	
	@Override
	public double get(final int row, final int col) {
		return data[row].get(col);
	}

	@Override
	public void set(final int row, final int col, final double value) {
		data[row].set(col, value);
	}
}