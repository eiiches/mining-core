package net.thisptr.lang.sequence;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class FloatArraySequence extends AbstractArraySequence {
	private float[] raw;
	private int begin;
	private int end;
	
	public FloatArraySequence(final int n) {
		this(new float[n]);
	}
	
	public FloatArraySequence(final float[] raw) {
		this(raw, 0, raw.length);
	}
	
	public FloatArraySequence(final float[] raw, final int begin, final int end) {
		assert 0 <= begin && begin <= raw.length;
		assert 0 <= end && end <= raw.length;
		this.raw = raw;
		this.begin = begin;
		this.end = end;
	}

	@Override
	public float floatValue(final int index) {
		return raw[begin + index];
	}

	@Override
	public void set(final int index, final float value) {
		raw[begin + index] = value;
	}

	@Override
	public FloatArraySequence view(final int begin, final int end) {
		return new FloatArraySequence(raw, this.begin + begin, this.begin + end);
	}
	
	public int length() {
		return end - begin;
	}
	
	public float[] floatArray() {
		return raw;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(ArrayUtils.subarray(raw, begin, end));
	}

	@Override
	public Sequence copy() {
		return new FloatArraySequence(Arrays.copyOfRange(raw, begin, end));
	}

	@Override
	public Sequence copy(int begin, int end) {
		return new FloatArraySequence(Arrays.copyOfRange(raw, this.begin + begin, this.begin + end));
	}
}