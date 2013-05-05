package net.thisptr.lang.sequence;

public interface Sequence {
	int length();
	
	boolean booleanValue(int index);
	byte byteValue(int index);
	char charValue(int index);
	double doubleValue(int index);
	float floatValue(int index);
	int intValue(int index);
	long longValue(int index);
	short shortValue(int index);
	
	void set(int index, boolean value);
	void set(int index, byte value);
	void set(int index, char value);
	void set(int index, double value);
	void set(int index, float value);
	void set(int index, int value);
	void set(int index, long value);
	void set(int index, short value);
	
	void fill(boolean value);
	void fill(byte value);
	void fill(char value);
	void fill(double value);
	void fill(float value);
	void fill(int value);
	void fill(long value);
	void fill(short value);

	Sequence view(int begin, int end);
	
	Sequence copy();
	Sequence copy(int begin, int end);
}
