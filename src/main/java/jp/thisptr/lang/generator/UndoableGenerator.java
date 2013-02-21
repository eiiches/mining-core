package jp.thisptr.lang.generator;

import java.util.Stack;

import jp.thisptr.lang.StopIteration;

public class UndoableGenerator<T> extends SinglyGenerator<T> {
	private SinglyGenerator<T> it;
	private Stack<T> undo = new Stack<T>();
	
	public UndoableGenerator(final SinglyGenerator<T> it) {
		this.it = it;
	}
	
	public void uninvoke(final T item) {
		undo.push(item);
	}
	
	@Override
	public T invoke() throws StopIteration {
		if (!undo.isEmpty())
			return undo.pop();
		return it.invoke();
	}
}