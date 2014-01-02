package net.thisptr.neuralnet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.thisptr.lang.tuple.Pair;
import net.thisptr.math.factory.MathFactory;
import net.thisptr.math.matrix.Matrix;

public class MatrixPool {
	private MathFactory factory;

	// FIXME: should allow GC.
	private Map<Pair<Integer, Integer>, LinkedList<Matrix>> pool = new HashMap<>();

	public MatrixPool(final MathFactory factory) {
		this.factory = factory;
	}

	public Matrix acquire(final int rows, final int columns) {
		final LinkedList<Matrix> freeList = pool.get(Pair.make(rows, columns));
		if (freeList == null || freeList.isEmpty())
			return factory.newDenseMatrix(rows, columns);
		return freeList.pop();
	}

	public void release(final Matrix m) {
		LinkedList<Matrix> freeList = pool.get(Pair.make(m.rows(), m.columns()));
		if (freeList == null) {
			freeList = new LinkedList<Matrix>();
			pool.put(Pair.make(m.rows(), m.columns()), freeList);
		}
		freeList.add(m);
	}

	public void release(final Matrix... ms) {
		for (final Matrix m : ms)
			release(m);
	}
}