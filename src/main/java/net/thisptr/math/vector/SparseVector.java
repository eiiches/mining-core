package net.thisptr.math.vector;

public abstract class SparseVector extends AbstractVector {
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("[");
		walk(new VectorVisitor() {
			private String sep = "";
			@Override
			public void visit(int index, double value) {
				builder.append(sep);
				builder.append(String.format("%d: %.2f", index, value));
				sep = ", ";
			}
		});
		builder.append("]");
		return builder.toString();
	}
}