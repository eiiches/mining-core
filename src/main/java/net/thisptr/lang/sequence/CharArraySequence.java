package net.thisptr.lang.sequence;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class CharArraySequence extends AbstractArraySequence {
	private char[] raw;
	private int begin;
	private int end;
	
	public CharArraySequence(final int n) {
		this(new char[n]);
	}
	
	public CharArraySequence(final char[] raw) {
		this(raw, 0, raw.length);
	}
	
	public CharArraySequence(final char[] raw, final int begin, final int end) {
		assert 0 <= begin && begin <= raw.length;
		assert 0 <= end && end <= raw.length;
		this.raw = raw;
		this.begin = begin;
		this.end = end;
	}
	
	@Override
	public char charValue(final int index) {
		return raw[begin + index];
	}
	
	@Override
	public int intValue(int index) {
		return charValue(index);
	}

	@Override
	public void set(final int index, final char value) {
		raw[begin + index] = value;
	}

	@Override
	public CharArraySequence view(final int begin, final int end) {
		return new CharArraySequence(raw, this.begin + begin, this.begin + end);
	}
	
	@Override
	public int length() {
		return end - begin;
	}
	
	public char[] charArray() {
		return raw;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(ArrayUtils.subarray(raw, begin, end));
	}

	@Override
	public void fill(char value) {
		Arrays.fill(raw, begin, end, value);
	}

	@Override
	public Sequence copy() {
		return new CharArraySequence(Arrays.copyOfRange(raw, begin, end));
	}

	@Override
	public Sequence copy(int begin, int end) {
		return new CharArraySequence(Arrays.copyOfRange(raw, this.begin + begin, this.begin + end));
	}
}