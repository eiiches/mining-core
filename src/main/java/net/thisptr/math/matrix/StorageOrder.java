package net.thisptr.math.matrix;

public enum StorageOrder {
	RowMajor, ColumnMajor;

	public StorageOrder transpose() {
		switch (this) {
			case ColumnMajor:
				return RowMajor;
			case RowMajor:
				return ColumnMajor;
		}
		throw new IllegalStateException();
	}
}