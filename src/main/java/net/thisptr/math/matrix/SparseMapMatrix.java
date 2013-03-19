package net.thisptr.math.matrix;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.thisptr.lang.NotImplementedException;
import net.thisptr.math.vector.SparseMapVector;
import net.thisptr.math.vector.Vector;

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

	@Override
	public int rowSize() {
		if (data.isEmpty())
			return 0;
		return Collections.max(data.keySet()) + 1;
	}

	@Override
	public int rowCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int colSize() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public int colCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void walk(final Visitor visitor) {
		for (final Map.Entry<Integer, SparseMapVector> row : data.entrySet()) {
			row.getValue().walk(new Vector.Visitor() {
				@Override
				public void visit(final int index, final double value) {
					visitor.visit(row.getKey(), index, value);
				}
			});
		}
	}

	@Override
	public Iterator<Element> iterator() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
}
