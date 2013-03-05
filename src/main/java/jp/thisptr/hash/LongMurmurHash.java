package jp.thisptr.hash;

import jp.thisptr.lang.NotImplementedException;

public final class LongMurmurHash {
	private LongMurmurHash() { }
	
	private static long fmix(long h) {
		h ^= h >>> 16;
		h *= 0x85ebca6b;
		h ^= h >>> 13;
		h *= 0xc2b2ae35;
		h ^= h >>> 16;
		return h;
	}
	
	public static long hash(final int i) {
		long h1 = 0xb0f57ee3;
		long c1 = 0xcc9e2d51;
		long c2 = 0x1b873593;
		long k1 = i;
		
		k1 *= c1;
		k1 = Long.rotateLeft(k1, 15);
		k1 *= c2;
		
		h1 ^= k1;
		h1 = Long.rotateLeft(h1, 13);
		h1 = h1 * 5 + 0xe6546b64;
		
		// finalize
		h1 ^= 4;
		h1 = fmix(h1);
		return h1;
	}
	
	public static int hash(final String s) {
		throw new NotImplementedException();
	}
}
