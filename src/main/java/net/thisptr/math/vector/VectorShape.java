package net.thisptr.math.vector;

public enum VectorShape {
	Column, Row;

	public VectorShape transpose() {
		switch (this) {
			case Column:
				return Row;
			case Row:
				return Column;
		}
		throw new IllegalArgumentException();
	}
}