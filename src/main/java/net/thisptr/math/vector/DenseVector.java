package net.thisptr.math.vector;

public abstract class DenseVector extends AbstractVector {
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("[");
		String sep = "";
		for (int i = 0; i < size(); ++i) {
			final double value = get(i);
			sb.append(String.format("%s%10f", sep, value));
			sep = ",";
		}
		sb.append("]");
		return sb.toString();
	}
}
