package net.thisptr.math.vector;

public abstract class VectorVisitor {
	public abstract void visit(final int index, final double value);
	public double finish() {
		return 0.0;
	}
}