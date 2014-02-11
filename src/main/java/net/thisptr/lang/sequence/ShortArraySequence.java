package net.thisptr.lang.sequence;

import java.util.Arrays;

public class ShortArraySequence extends AbstractArraySequence {
	private short[] raw;
	private int begin;
	private int end;

	public ShortArraySequence(final int n) {
		this(new short[n]);
	}

	public ShortArraySequence(final short[] raw) {
		this(raw, 0, raw.length);
	}

	public ShortArraySequence(final short[] raw, final int begin, final int end) {
		assert 0 <= begin && begin <= raw.length;
		assert 0 <= end && end <= raw.length;
		this.raw = raw;
		this.begin = begin;
		this.end = end;
	}

	@Override
	public short shortValue(final int index) {
		return raw[begin + index];
	}

	@Override
	public void set(final int index, final short value) {
		raw[begin + index] = value;
	}

	@Override
	public ShortArraySequence view(final int begin, final int end) {
		return new ShortArraySequence(raw, this.begin + begin, this.begin + end);
	}

	public int length() {
		return end - begin;
	}

	public short[] shortArray() {
		return raw;
	}

	@Override
	public String toString() {
		return Arrays.toString(Arrays.copyOfRange(raw, begin, end));
	}

	@Override
	public void fill(short value) {
		Arrays.fill(raw, begin, end, value);
	}

	@Override
	public Sequence copy() {
		return new ShortArraySequence(Arrays.copyOfRange(raw, begin, end));
	}

	@Override
	public Sequence copy(int begin, int end) {
		return new ShortArraySequence(Arrays.copyOfRange(raw, this.begin + begin, this.begin + end));
	}
}