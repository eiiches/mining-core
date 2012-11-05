package jp.thisptr.core.lambda;

import java.lang.reflect.Method;

public abstract class Lambda1<R, A1> extends LambdaBase<R, Lambda1<R, A1>> {
	public abstract R invoke(A1 arg1);
	
	public <ChainResultType> Lambda1<ChainResultType, A1> chain(final Lambda1<ChainResultType, ? super R> g) {
		final Lambda1<R, A1> dependant = this;
		return new Lambda1<ChainResultType, A1>() {
			public ChainResultType invoke(final A1 arg1) {
				return g.invoke(dependant.invoke(arg1));
			}
		};
	}
	
	public Lambda0<R> bind(final A1 arg1) {
		final Lambda1<R, A1> original = this;
		return new Lambda0<R>() {
			public R invoke() {
				return original.invoke(arg1);
			}
		};
	}
	
	public static final <T, U> Lambda1<T, U> fromMethod(final Method method, final Object instance) {
		return new Lambda1<T, U>() {
			@SuppressWarnings("unchecked")
			public T invoke(final U arg1) {
				try {
					return (T) method.invoke(instance, arg1);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	public static final <T, U> Lambda1<T, U> fromStaticMethod(final Method method) {
		return fromMethod(method, null);
	}
}
