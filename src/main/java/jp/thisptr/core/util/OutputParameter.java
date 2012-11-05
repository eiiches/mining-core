package jp.thisptr.core.util;

import java.util.ArrayList;
import java.util.List;

import jp.thisptr.core.generator.SinglyGenerator;
import jp.thisptr.core.generator.Generators;

public class OutputParameter<T> {
	private List<T> value = new ArrayList<T>();
	public void add(final T value) {
		this.value.add(value);
	}
	public SinglyGenerator<T> get() {
		return Generators.array(value);
	}
}
