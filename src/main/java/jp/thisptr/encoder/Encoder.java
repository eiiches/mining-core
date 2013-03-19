package jp.thisptr.encoder;

import jp.thisptr.math.vector.Vector;

public interface Encoder<T> {
	Vector encode(final T record);
}