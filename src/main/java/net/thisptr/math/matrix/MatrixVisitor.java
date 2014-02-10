package net.thisptr.math.matrix;

public interface MatrixVisitor {
	void visit(int row, int column, double value);
}