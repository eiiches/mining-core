package jp.thisptr.core.generator;

import jp.thisptr.core.generator.signal.StopIteration;
import jp.thisptr.core.lambda.Lambda0;

public abstract class AbstractGeneratorCore<T> extends Lambda0<T> {
	public abstract T invoke() throws StopIteration;
	public static final StopIteration stop = new StopIteration();
}