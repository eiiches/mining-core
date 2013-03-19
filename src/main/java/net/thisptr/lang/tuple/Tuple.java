package net.thisptr.lang.tuple;

import java.util.Arrays;
import java.util.Iterator;

public class Tuple implements Iterable<Object> {
	private final Object[] values;
	
	public Tuple(final Object... values) {
		this.values = values;
	}
	
	public <T> T get(final int index, final Class<T> klass) {
		return klass.cast(values[index]);
	}
	
	public Object get(final int index) {
		return values[index];
	}
	
	public int size() {
		return values.length;
	}

	@Override
	public Iterator<Object> iterator() {
		/* FIXME: do not create list here */
		return Arrays.asList(values).iterator();
	}

	@Override
	public String toString() {
		String sep = "";
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		for (Object value : values) {
			builder.append(sep);
			builder.append(value.toString());
			sep = ",";
		}
		builder.append(")");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple other = (Tuple) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}
	
	public static Tuple make(final Object... objects) {
		return new Tuple(objects);
	}
}