package net.thisptr.math.factory;

import net.thisptr.math.matrix.Matrix;
import net.thisptr.math.operator.MathOperator;
import net.thisptr.math.vector.Vector;

public interface MathFactory {
	Matrix newDenseMatrix(int rows, int columns);
	Matrix newSparseMatrix(int rows, int columns);
	Vector newDenseVector(int size);
	Vector newSparseVector(int size);
	MathOperator newMathOperator();
}
