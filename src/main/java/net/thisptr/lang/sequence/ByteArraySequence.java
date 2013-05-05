package net.thisptr.lang.sequence;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class ByteArraySequence extends AbstractArraySequence {
	private byte[] raw;
	private int begin;
	private int end;
	
	public ByteArraySequence(final int n) {
		this(new byte[n]);
	}
	
	public ByteArraySequence(final byte[] raw) {
		this(raw, 0, raw.length);
	}
	
	public ByteArraySequence(final byte[] raw, final int begin, final int end) {
		assert 0 <= begin && begin <= raw.length;
		assert 0 <= end && end <= raw.length;
		this.raw = raw;
		this.begin = begin;
		this.end = end;
	}

	@Override
	public byte byteValue(int index) {
		return raw[begin + index];
	}

	@Override
	public void set(final int index, final byte value) {
		raw[begin + index] = value;
	}

	@Override
	public ByteArraySequence view(final int begin, final int end) {
		return new ByteArraySequence(raw, this.begin + begin, this.begin + end);
	}
	
	public int length() {
		return end - begin;
	}
	
	public byte[] byteArray() {
		return raw;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(ArrayUtils.subarray(raw, begin, end));
	}

	@Override
	public void fill(byte value) {
		Arrays.fill(raw, begin, end, value);
	}

	@Override
	public Sequence copy() {
		return new ByteArraySequence(Arrays.copyOfRange(raw, begin, end));
	}

	@Override
	public Sequence copy(int begin, int end) {
		return new ByteArraySequence(Arrays.copyOfRange(raw, this.begin + begin, this.begin + end));
	}
}