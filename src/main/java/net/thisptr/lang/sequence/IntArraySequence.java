package net.thisptr.lang.sequence;

import java.util.Arrays;

public class IntArraySequence extends AbstractArraySequence {
	private int[] raw;
	private int begin;
	private int end;

	public IntArraySequence(final int n) {
		this(new int[n], 0, n);
	}

	public IntArraySequence(final int[] raw) {
		this(raw, 0, raw.length);
	}

	public IntArraySequence(final int[] raw, final int begin, final int end) {
		assert 0 <= begin && begin <= raw.length;
		assert 0 <= end && end <= raw.length;
		this.raw = raw;
		this.begin = begin;
		this.end = end;
	}

	@Override
	public IntArraySequence view(final int begin, final int end) {
		return new IntArraySequence(raw, this.begin + begin, this.begin + end);
	}

	@Override
	public int length() {
		return end - begin;
	}

	public int[] intArray() {
		return raw;
	}

	@Override
	public String toString() {
		return Arrays.toString(Arrays.copyOfRange(raw, begin, end));
	}

	@Override
	public Sequence copy() {
		return new IntArraySequence(Arrays.copyOfRange(raw, begin, end));
	}

	@Override
	public Sequence copy(int begin, int end) {
		return new IntArraySequence(Arrays.copyOfRange(raw, this.begin + begin, this.begin + end));
	}

	@Override
	public void fill(int value) {
		Arrays.fill(raw, begin, end, value);
	}

	@Override
	public void set(final int index, final int value) {
		raw[begin + index] = value;
	}

	@Override
	public int intValue(final int index) {
		return raw[begin + index];
	}
}