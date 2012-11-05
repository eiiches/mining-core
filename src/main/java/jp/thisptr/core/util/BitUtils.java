package jp.thisptr.core.util;

import java.util.BitSet;

public final class BitUtils {
	private BitUtils() { }
	
	public static BitSet intToBitSet(final int value) {
		BitSet result = new BitSet(Integer.SIZE);
		for (int i = 0; i < Integer.SIZE; ++i)
			if (((value >> i) & 1) != 0)
				result.set(i);
		return result;
	}
	
	public static int bitSetToInt(final BitSet value) {
		int result = 0;
		for (int i = 0; i < Integer.SIZE; ++i) {
			result |= (value.get(i) ? 1 : 0) << i;
		}
		return result;
	}
}
