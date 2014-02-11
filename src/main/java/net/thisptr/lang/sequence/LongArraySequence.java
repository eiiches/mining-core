package net.thisptr.lang.sequence;

import java.util.Arrays;

public class LongArraySequence extends AbstractArraySequence {
	private long[] raw;
	private int begin;
	private int end;

	public LongArraySequence(final int n) {
		this(new long[n]);
	}

	public LongArraySequence(final long[] raw) {
		this(raw, 0, raw.length);
	}

	public LongArraySequence(final long[] raw, final int begin, final int end) {
		assert 0 <= begin && begin <= raw.length;
		assert 0 <= end && end <= raw.length;
		this.raw = raw;
		this.begin = begin;
		this.end = end;
	}

	@Override
	public void set(final int index, final long value) {
		raw[begin + index] = value;
	}

	@Override
	public LongArraySequence view(final int begin, final int end) {
		return new LongArraySequence(raw, this.begin + begin, this.begin + end);
	}

	public int length() {
		return end - begin;
	}

	public long[] longArray() {
		return raw;
	}

	@Override
	public String toString() {
		return Arrays.toString(Arrays.copyOfRange(raw, begin, end));
	}

	@Override
	public void fill(long value) {
		Arrays.fill(raw, begin, end, value);
	}

	@Override
	public Sequence copy() {
		return new LongArraySequence(Arrays.copyOfRange(raw, begin, end));
	}

	@Override
	public Sequence copy(int begin, int end) {
		return new LongArraySequence(Arrays.copyOfRange(raw, this.begin + begin, this.begin + end));
	}
}