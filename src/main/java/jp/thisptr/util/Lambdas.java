package jp.thisptr.util;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.thisptr.lang.UnsupportedTypeException;
import jp.thisptr.lang.lambda.Lambda0;
import jp.thisptr.lang.lambda.Lambda1;
import jp.thisptr.lang.lambda.Lambda2;
import jp.thisptr.lang.lambda.alias.BinaryFunction;
import jp.thisptr.lang.lambda.alias.Lambda;
import jp.thisptr.lang.tuple.Pair;
import jp.thisptr.lang.tuple.Tuple;
import jp.thisptr.util.experimental.ReflectUtils;

public final class Lambdas {
	private Lambdas() { }
	
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
	
	private static Lambda1<Long, String> _stringToLong = valueOf(Long.class);
	private static Lambda1<Integer, String> _stringToInteger = valueOf(Integer.class);
	private static Lambda1<Double, String> _stringToDouble = valueOf(Double.class);
	
	public static Lambda1<Integer, String> toInteger() {
		return _stringToInteger;
	}
	
	public static Lambda1<Long, String> toLong() {
		return _stringToLong;
	}
	
	public static Lambda1<Double, String> toDouble() {
		return _stringToDouble;
	}
	
	public static <T> Lambda1<T, String> valueOf(final Class<T> klass) {
		return fromStaticMethod(ReflectUtils.getMethod(klass, "valueOf", String.class));
	}
	
	private static Lambda1<Integer, Integer> _incrementInteger = new Lambda1<Integer, Integer>() {
		public Integer invoke(final Integer value) {
			return value + 1;
		}
	};
	
	public static Lambda1<Integer, Integer> increment() {
		return _incrementInteger;
	}
	
	public static <K, V> Lambda1<V, K> mapper(final Map<K, V> map) {
		return new Lambda1<V, K>() {
			public V invoke(final K key) {
				return map.get(key);
			}
		};
	}
	
	public static Lambda1<Class<?>, Object> getKlass() {
		return new Lambda1<Class<?>, Object>() {
			public Class<?> invoke(final Object obj) {
				return obj.getClass();
			}
		};
	}
	
	public static <T> Lambda0<T> constructor(final Class<T> klass, final Object... args) {
		return new Lambda0<T>() {
			public T invoke() {
				try {
					Class<?>[] types = CollectionUtils.map(args, Lambdas.getKlass()).toArray(new Class<?>[args.length]);
					return klass.getConstructor(types).newInstance(args);
				} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	public static <T> Lambda2<Boolean, T, T> equals() {
		return new Lambda2<Boolean, T, T>() {
			public Boolean invoke(final T t1, final T t2) {
				return t1.equals(t2);
			}
		};
	}
	
	public static <T> Lambda1<T, T> identity() {
		return new Lambda1<T, T>() {
			public T invoke(final T v) {
				return v;
			}
		};
	}
	
	public static <T> Lambda0<T> constant(final T v) {
		return Lambdas.<T>identity().bind(v);
	}
	
	public static <T extends Comparable<T>> BinaryFunction<T, T, T> max() {
		return new BinaryFunction<T, T, T>() {
			public T invoke(final T arg1, final T arg2) {
				return arg1.compareTo(arg2) >= 0 ? arg1 : arg2;
			}
		};
	}
	
	public static <T extends Comparable<T>> BinaryFunction<T, T, T> max(final Class<T> klass) {
		return Lambdas.<T>max();
	}
	
	private abstract static class BinaryOperatorSet {
		public abstract Integer invoke(Integer a, Integer b);
		public abstract Double invoke(Double a, Double b);
		public abstract Long invoke(Long a, Long b);
		public abstract Float invoke(Float a, Float b);
		public abstract Byte invoke(Byte a, Byte b);
		public abstract Short invoke(Short a, Short b);
	}
	
	private abstract static class BinaryOperator<R, A> extends BinaryFunction<R, A, A> {
		public static Object invoke(final BinaryOperatorSet operator, final Number a, final Number b) {
			// TODO: performance optimization
			try {
				Method method = operator.getClass().getDeclaredMethod("invoke", a.getClass(), b.getClass());
				return method.getReturnType().cast(method.invoke(operator, a, b));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
				throw new UnsupportedTypeException();
			}
		}
		@SuppressWarnings("unchecked")
		public static <T extends Number> BinaryOperator<T, T> make(final BinaryOperatorSet operator) {
			return BinaryOperator.class.cast(new BinaryOperator<T, T>() {
				public T invoke(final T arg1, final T arg2) {
					return (T) BinaryOperator.invoke(operator, arg1, arg2);
				}
			});
		}
	}
	
	public static Lambda<Void, Object> println(final PrintStream ps) {
		return printf(ps, "%s%n");
	}
	public static Lambda<Void, Object> printf(final PrintStream ps, final String format) {
		return new Lambda<Void, Object>() {
			public Void invoke(final Object arg) {
				ps.printf(format, arg);
				return null;
			}
		};
	}
	
	private static class BinaryOperatorSetMul extends BinaryOperatorSet {
		public Integer invoke(final Integer a, final Integer b) { return a * b; }
		public Double invoke(final Double a, final Double b) { return a * b; }
		public Long invoke(final Long a, final Long b) { return a * b; }
		public Float invoke(final Float a, final Float b) { return a * b; }
		public Byte invoke(final Byte a, final Byte b) { return (byte) (a * b); }
		public Short invoke(final Short a, final Short b) { return (short) (a * b); }
	}
	public static <T extends Number> BinaryFunction<T, T, T> mul() { return BinaryOperator.make(new BinaryOperatorSetMul()); }
	public static <T extends Number> BinaryFunction<T, T, T> mul(final Class<T> klass) { return Lambdas.<T>mul(); }
	
	private static class BinaryOperatorSetAdd extends BinaryOperatorSet {
		public Integer invoke(final Integer a, final Integer b) { return a + b; }
		public Double invoke(final Double a, final Double b) { return a + b; }
		public Long invoke(final Long a, final Long b) { return a + b; }
		public Float invoke(final Float a, final Float b) { return a + b; }
		public Byte invoke(final Byte a, final Byte b) { return (byte) (a + b); }
		public Short invoke(final Short a, final Short b) { return (short) (a + b); }
	}
	public static <T extends Number> BinaryFunction<T, T, T> add() { return BinaryOperator.make(new BinaryOperatorSetAdd()); }
	public static <T extends Number> BinaryFunction<T, T, T> add(final Class<T> klass) { return Lambdas.<T>add(); }
	
	public static <T, U> Lambda<T, Pair<T, U>> getFirst() {
		return new Lambda<T, Pair<T, U>>() {
			public T invoke(final Pair<T, U> pair) {
				return pair.getFirst();
			}
		};
	}
	
	public static <T, U> Lambda<U, Pair<T, U>> getSecond() {
		return new Lambda<U, Pair<T, U>>() {
			public U invoke(final Pair<T, U> pair) {
				return pair.getSecond();
			}
		};
	}
	
	public static Lambda0<Long> counter() {
		return new Lambda0<Long>() {
			private long value = 0;
			public Long invoke() {
				return ++value;
			}
		};
	}

	public static <U, T> Lambda1<List<U>, List<T>> batch(final Lambda1<U, T> f) {
		return new Lambda1<List<U>, List<T>>() {
			public List<U> invoke(final List<T> items) {
				List<U> result = new ArrayList<U>();
				for (T item : items)
					result.add(f.invoke(item));
				return result;
			}
		};
	}
	
	@SafeVarargs
	public static <T> Lambda1<Tuple, T> tuple(final Lambda1<?, T>... fs) {
		return new Lambda1<Tuple, T>() {
			public Tuple invoke(final T arg) {
				Object[] objects = new Object[fs.length];
				for (int i = 0; i < objects.length; ++i)
					objects[i] = fs[i].invoke(arg);
				return Tuple.make(objects);
			}
		};
	}
	
	public static <T, U, V> Lambda1<Pair<U, V>, T> zip(final Lambda1<U, T> first, final Lambda1<V, T> second) {
		return new Lambda1<Pair<U, V>, T>() {
			public Pair<U, V> invoke(final T arg) {
				return Pair.make(first.invoke(arg), second.invoke(arg));
			}
		};
	}
}