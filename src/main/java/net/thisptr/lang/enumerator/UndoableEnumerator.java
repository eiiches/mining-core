package net.thisptr.lang.enumerator;

import java.util.Stack;

import net.thisptr.lang.StopIteration;

public class UndoableEnumerator<T> extends SinglyEnumerator<T> {
	private SinglyEnumerator<T> it;
	private Stack<T> undo = new Stack<T>();
	
	public UndoableEnumerator(final SinglyEnumerator<T> it) {
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