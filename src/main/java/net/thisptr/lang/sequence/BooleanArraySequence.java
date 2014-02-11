package net.thisptr.lang.sequence;

import java.util.Arrays;

public class BooleanArraySequence extends AbstractArraySequence {
	private boolean[] raw;
	private int begin;
	private int end;
	
	public BooleanArraySequence(final int n) {
		this(new boolean[n]);
	}
	
	public BooleanArraySequence(final boolean[] raw) {
		this(raw, 0, raw.length);
	}
	
	public BooleanArraySequence(final boolean[] raw, final int begin, final int end) {
		assert 0 <= begin && begin <= raw.length;
		assert 0 <= end && end <= raw.length;
		this.raw = raw;
		this.begin = begin;
		this.end = end;
	}

	@Override
	public void set(final int index, final boolean value) {
		raw[begin + index] = value;
	}

	@Override
	public BooleanArraySequence view(final int begin, final int end) {
		return new BooleanArraySequence(raw, this.begin + begin, this.begin + end);
	}
	
	public int length() {
		return end - begin;
	}
	
	public boolean[] booleanArray() {
		return raw;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(Arrays.copyOfRange(raw, begin, end));
	}

	@Override
	public void fill(boolean value) {
		Arrays.fill(raw, begin, end, value);
	}

	@Override
	public Sequence copy() {
		return new BooleanArraySequence(Arrays.copyOfRange(raw, begin, end));
	}

	@Override
	public Sequence copy(int begin, int end) {
		return new BooleanArraySequence(Arrays.copyOfRange(raw, this.begin + begin, this.begin + end));
	}
}