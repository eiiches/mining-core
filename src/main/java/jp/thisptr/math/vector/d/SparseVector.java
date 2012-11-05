package jp.thisptr.math.vector.d;

public abstract class SparseVector extends Vector {
	public abstract IndexedValue[] values();
	
	public static class IndexedValue {
		private final int index;
		private final double value;
		public IndexedValue(final int index, final double value) {
			this.index = index;
			this.value = value;
		}
		public double getValue() {
			return value;
		}
		public int getIndex() {
			return index;
		}
	}
}
