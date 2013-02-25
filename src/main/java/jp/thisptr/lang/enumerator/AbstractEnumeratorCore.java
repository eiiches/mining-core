package jp.thisptr.lang.enumerator;

import jp.thisptr.lang.StopIteration;
import jp.thisptr.lang.lambda.Lambda0;

public abstract class AbstractEnumeratorCore<T> extends Lambda0<T> {
	public abstract T invoke() throws StopIteration;
	public static final StopIteration stop = new StopIteration();
}