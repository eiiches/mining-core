package jp.thisptr.lang.generator;

import jp.thisptr.lang.StopIteration;
import jp.thisptr.lang.lambda.Lambda0;

public abstract class AbstractGeneratorCore<T> extends Lambda0<T> {
	public abstract T invoke() throws StopIteration;
	public static final StopIteration stop = new StopIteration();
}