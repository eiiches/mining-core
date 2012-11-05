package jp.thisptr.core.generator;

public interface Generator<T> extends Iterable<T> {
	ChunkedGenerator<T> chunk(final int chunkSize);
	SinglyGenerator<T> unchunk();
}