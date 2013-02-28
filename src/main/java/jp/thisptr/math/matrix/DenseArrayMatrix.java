package jp.thisptr.math.matrix;

import java.util.Iterator;

import jp.thisptr.math.vector.DenseArrayVector;

import org.apache.commons.lang.NotImplementedException;

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

	@Override
	public int rowSize() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public int rowCapacity() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public int colSize() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public int colCapacity() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public void walk(final Visitor visitor) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public Iterator<Element> iterator() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
}