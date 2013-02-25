package jp.thisptr.lang.enumerator;

import java.util.Iterator;

public final class IteratorUtils {
	private IteratorUtils() { }
	
	public <T> Iterable<T> asIterable(final Iterator<T> iter) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return iter;
			}
		};
	}
}
