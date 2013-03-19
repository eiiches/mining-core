package net.thisptr.lang.enumerator;

import net.thisptr.lang.StopIteration;
import net.thisptr.lang.lambda.Lambda0;

public abstract class AbstractEnumeratorCore<T> extends Lambda0<T> {
	public abstract T invoke() throws StopIteration;
	public static final StopIteration stop = new StopIteration();
}