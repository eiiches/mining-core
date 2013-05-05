package net.thisptr.string.suffixarray;

import java.util.Arrays;


public class SuffixArray {
	private final int[] intArray;
	
	public SuffixArray(final int[] sa) {
		this.intArray = sa;
	}
	
	public int length() {
		return intArray.length;
	}
	
	public int intValue(final int index) {
		return intArray[index];
	}
	
	public int[] intArray() {
		return intArray;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(intArray);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SuffixArray other = (SuffixArray) obj;
		if (!Arrays.equals(intArray, other.intArray))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return Arrays.toString(intArray);
	}
}