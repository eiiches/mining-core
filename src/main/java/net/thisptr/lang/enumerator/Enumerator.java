package net.thisptr.lang.enumerator;

public interface Enumerator<T> extends Iterable<T> {
	ChunkedEnumerator<T> chunk(final int chunkSize);
	SinglyEnumerator<T> unchunk();
}