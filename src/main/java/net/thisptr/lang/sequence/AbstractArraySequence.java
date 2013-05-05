package net.thisptr.lang.sequence;

import net.thisptr.lang.UnsupportedTypeException;

public abstract class AbstractArraySequence implements Sequence {
	@Override
	public void set(int index, boolean value) {
		set(index, (byte) (value ? 1: 0));
	}

	@Override
	public void set(int index, byte value) {
		set(index, (short) value);
	}

	@Override
	public void set(int index, char value) {
		set(index, (int) value);
	}

	@Override
	public void set(int index, float value) {
		set(index, (double) value);
	}

	@Override
	public void set(int index, int value) {
		set(index, (long) value);
	}

	@Override
	public void set(int index, short value) {
		set(index, (int) value);
	}
	
	@Override
	public void set(int index, double value) {
		throw new UnsupportedTypeException();
	}
	
	@Override
	public void set(int index, long value) {
		throw new UnsupportedTypeException();
	}

	@Override
	public void fill(boolean value) {
		fill((byte) (value ? 1 : 0));
	}

	@Override
	public void fill(byte value) {
		fill((short) value);
	}

	@Override
	public void fill(char value) {
		fill((int) value);
	}

	@Override
	public void fill(float value) {
		fill((double) value);
	}

	@Override
	public void fill(int value) {
		fill((long) value);
	}

	@Override
	public void fill(short value) {
		fill((int) value);
	}
	
	@Override
	public void fill(double value) {
		throw new UnsupportedTypeException();
	}
	
	@Override
	public void fill(long value) {
		throw new UnsupportedTypeException();
	}
	
	@Override
	public boolean booleanValue(int index) {
		throw new UnsupportedTypeException();
	}
	
	@Override
	public byte byteValue(int index) {
		return (byte) (booleanValue(index) ? 1 : 0);
	}
	
	@Override
	public char charValue(int index) {
		return (char) byteValue(index);
	}
	
	@Override
	public float floatValue(int index) {
		throw new UnsupportedTypeException();
	}
	
	@Override
	public int intValue(int index) {
		return shortValue(index);
	}
	
	@Override
	public short shortValue(int index) {
		return byteValue(index);
	}
	
	@Override
	public double doubleValue(int index) {
		return floatValue(index);
	}
	
	@Override
	public long longValue(int index) {
		return intValue(index);
	}
}
