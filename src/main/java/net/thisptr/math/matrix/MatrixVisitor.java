package net.thisptr.math.matrix;

public abstract class MatrixVisitor {
	public abstract void visit(int row, int column, double value);
	public double finish() {
		return 0.0;
	}
}