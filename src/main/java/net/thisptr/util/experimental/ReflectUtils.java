package net.thisptr.util.experimental;

import java.lang.reflect.Method;

public final class ReflectUtils {
	private ReflectUtils() { }
	
	public static <T> Method getMethod(final Class<T> klass, final String declaredName, final Class<?>... parameterTypes) {
		try {
			return klass.getMethod(declaredName, parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
}
