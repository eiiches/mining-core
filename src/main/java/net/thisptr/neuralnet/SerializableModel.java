package net.thisptr.neuralnet;

import java.nio.ByteBuffer;

public interface SerializableModel {
	void load(ByteBuffer buffer);

	ByteBuffer serialize();
}
