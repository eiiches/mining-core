package net.thisptr.math.factory;

import net.thisptr.math.matrix.DenseArrayMatrix;
import net.thisptr.math.matrix.Matrix;
import net.thisptr.math.matrix.SparseMapMatrix;
import net.thisptr.math.operator.DefaultMathOperator;
import net.thisptr.math.operator.MathOperator;
import net.thisptr.math.vector.DenseArrayVector;
import net.thisptr.math.vector.SparseMapVector;
import net.thisptr.math.vector.Vector;

public class DefaultMathFactory implements MathFactory {
	@Override
	public Matrix newDenseMatrix(int rows, int columns) {
		return new DenseArrayMatrix(rows, columns);
	}

	@Override
	public Matrix newSparseMatrix(int rows, int columns) {
		return new SparseMapMatrix(rows, columns);
	}

	@Override
	public Vector newDenseVector(int size) {
		return new DenseArrayVector(size);
	}

	@Override
	public Vector newSparseVector(int size) {
		return new SparseMapVector(size);
	}

	@Override
	public MathOperator newMathOperator() {
		return new DefaultMathOperator();
	}
}
