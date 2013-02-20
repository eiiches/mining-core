package jp.thisptr.math.vector;

public abstract class DenseVector extends Vector {
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("[");
		String sep = "";
		for (int i = 0; i < size(); ++i) {
			sb.append(String.format("%s%.10f", sep, get(i)));
			sep = ",";
		}
		sb.append("]");
		return sb.toString();
	}
}
