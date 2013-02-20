package jp.thisptr.util;

import jp.thisptr.lang.lambda.Lambda0;

public class Cache<T> extends Lambda0<T> {
	private Lambda0<T> f;
	private T value;
	private boolean isValid = false;
	
	public Cache(final Lambda0<T> f) {
		this.f = f;
	}
	
	public T get() {
		if (!isValid) {
			value = f.invoke();
			isValid = true;
		}
		return value;
	}
	
	public T invoke() {
		return get();
	}
	
	public void invalidate() {
		isValid = false;
	}
}
