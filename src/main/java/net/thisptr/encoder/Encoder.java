package net.thisptr.encoder;

import net.thisptr.math.vector.Vector;

public interface Encoder<T> {
	Vector encode(final T record);
}