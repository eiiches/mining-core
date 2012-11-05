package jp.thisptr.hash;

import org.apache.commons.lang.StringUtils;


public final class IntMurmurHash {
	private IntMurmurHash() { }
	
	private static final int seed = 0xb0f57ee3;
	private static final int c1 = 0xcc9e2d51;
	private static final int c2 = 0x1b873593;
	
	private static int fmix(int h) {
		h ^= h >>> 16;
		h *= 0x85ebca6b;
		h ^= h >>> 13;
		h *= 0xc2b2ae35;
		h ^= h >>> 16;
		return h;
	}
	
	private static int shuffle(int k) {
		k *= c1;
		k = Integer.rotateLeft(k, 15);
		k *= c2;
		return k;
	}
	
	private static int remainUpdate(int h, int k) {
		h ^= shuffle(k);
		return h;
	}
	
	private static int blockUpdate(int h, int k) {
		h ^= shuffle(k);
		h = Integer.rotateLeft(h, 13);
		h = h * 5 + 0xe6546b64;
		return h;
	}
	
	private static int finalize(int h, int len) {
		h ^= len;
		h = fmix(h);
		return h;
	}
	
	public static int hash(final int i) {
		int h = seed;
		h = blockUpdate(h, i);
		return finalize(h, 4);
	}
	
	public static int hash(final int[] data) {
		int h = seed;
		
		for (int i = 0; i < data.length; ++i)
			h = blockUpdate(h, data[i]);
		
		return finalize(h, data.length * 4);
	}
	
	public static int hash(final char[] data) {
		int h = seed;
		
		int i = 0;
		while (i + 1 < data.length) { 
			h = blockUpdate(h, data[i] ^ (data[i + 1] << Character.SIZE));
			i += 2;
		}
		
		int k = 0;
		switch (data.length - i) {
		case 1:
			k ^= data[i];
			h = remainUpdate(h, k);
		}
		
		return finalize(h, data.length * 2);
	}
	
	public static int hash(final byte[] data) {
		int h = seed;
		
		int i = 0;
		while (i + 3 < data.length) {
			h = blockUpdate(h, data[i] ^ (data[i + 1] << Byte.SIZE) ^ (data[i + 2] << (Byte.SIZE * 2)) ^ (data[i + 3] << (Byte.SIZE * 3)));
			i += 4;
		}
		
		int k = 0;
		switch (data.length - i) {
		case 3:
			k ^= data[i + 2] << Byte.SIZE * 2;
		case 2:
			k ^= data[i + 1] << Byte.SIZE;
		case 1:
			k ^= data[i];
			h = remainUpdate(h, k);
		}
		
		return finalize(h, data.length);
	}
	
	public static int hash(final String s) {
		return hash(s.toCharArray());
	}
}