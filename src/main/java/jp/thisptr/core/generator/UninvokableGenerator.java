package jp.thisptr.core.generator;

import java.util.Stack;

import jp.thisptr.core.generator.signal.StopIteration;

public class UninvokableGenerator<T> extends SinglyGenerator<T> {
	private SinglyGenerator<T> it;
	private Stack<T> uninvokeStack = new Stack<>();
	
	public UninvokableGenerator(final SinglyGenerator<T> it) {
		this.it = it;
	}
	
	public void uninvoke(final T item) {
		uninvokeStack.push(item);
	}
	
	@Override
	public T invoke() throws StopIteration {
		if (!uninvokeStack.isEmpty())
			return uninvokeStack.pop();
		return it.invoke();
	}
}