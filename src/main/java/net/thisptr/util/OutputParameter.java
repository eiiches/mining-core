package net.thisptr.util;

import java.util.ArrayList;
import java.util.List;

import net.thisptr.lang.enumerator.Enumerators;
import net.thisptr.lang.enumerator.SinglyEnumerator;

public class OutputParameter<T> {
	private List<T> value = new ArrayList<T>();
	public void add(final T value) {
		this.value.add(value);
	}
	public SinglyEnumerator<T> get() {
		return Enumerators.array(value);
	}
}
