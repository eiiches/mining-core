package net.thisptr.lang.sequence;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class DoubleArraySequence extends AbstractArraySequence {
	private double[] raw;
	private int begin;
	private int end;
	
	public DoubleArraySequence(final int n) {
		this(new double[n]);
	}
	
	public DoubleArraySequence(final double[] raw) {
		this(raw, 0, raw.length);
	}
	
	public DoubleArraySequence(final double[] raw, final int begin, final int end) {
		assert 0 <= begin && begin <= raw.length;
		assert 0 <= end && end <= raw.length;
		this.raw = raw;
		this.begin = begin;
		this.end = end;
	}
	
	@Override
	public double doubleValue(int index) {
		return raw[begin + index];
	}

	@Override
	public void set(final int index, final double value) {
		raw[begin + index] = value;
	}

	@Override
	public DoubleArraySequence view(final int begin, final int end) {
		return new DoubleArraySequence(raw, this.begin + begin, this.begin + end);
	}
	
	public int length() {
		return end - begin;
	}
	
	public double[] doubleArray() {
		return raw;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(ArrayUtils.subarray(raw, begin, end));
	}

	@Override
	public void fill(double value) {
		Arrays.fill(raw, begin, end, value);
	}

	@Override
	public Sequence copy() {
		return new DoubleArraySequence(Arrays.copyOfRange(raw, begin, end));
	}

	@Override
	public Sequence copy(int begin, int end) {
		return new DoubleArraySequence(Arrays.copyOfRange(raw, this.begin + begin, this.begin + end));
	}
}