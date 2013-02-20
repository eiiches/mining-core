package jp.thisptr.util;

import java.util.ArrayList;
import java.util.List;

import jp.thisptr.lang.generator.Generators;
import jp.thisptr.lang.generator.SinglyGenerator;

public class OutputParameter<T> {
	private List<T> value = new ArrayList<T>();
	public void add(final T value) {
		this.value.add(value);
	}
	public SinglyGenerator<T> get() {
		return Generators.array(value);
	}
}
