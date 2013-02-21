package jp.thisptr.lang.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jp.thisptr.lang.StopIteration;
import jp.thisptr.lang.ValueError;
import jp.thisptr.lang.lambda.Lambda0;
import jp.thisptr.lang.tuple.Pair;
import jp.thisptr.lang.tuple.Tuple;

public final class Generators {
	private Generators() { }
	
	public static SinglyGenerator<Integer> fibonatti() {
		return new SinglyGenerator<Integer>() {
			private int a = 1;
			private int b = 1;
			public Integer invoke() throws StopIteration {
				int result = a;
				int b1 = a + b;
				int a1 = b;
				a = a1;
				b = b1;
				return result;
			}
		};
	}
	
	public static SinglyGenerator<Integer> sequence() {
		return new SinglyGenerator<Integer>() {
			private int current = 0;
			public Integer invoke() throws StopIteration {
				return current++;
			}
		};
	}
	
	public static SinglyGenerator<Integer> sequence(final int n) {
		return sequence(0, n);
	}
	
	public static SinglyGenerator<Integer> sequence(final int first, final int last) {
		int step = last >= first ? 1 : -1;
		return sequence(first, last, step);
	}
	
	public static SinglyGenerator<Integer> sequence(final int first, final int last, final int step) {
		if (step == 0)
			throw new ValueError();
		return new SinglyGenerator<Integer>() {
			private int current = first;
			public Integer invoke() {
				if (step > 0 && current < last || step < 0 && current > last) {
					int result = current;
					current += step;
					return result;
				}
				throw stop;
			}
		};
	}
	
	public static SinglyGenerator<Double> sequence(final double first, final double last, final double step) {
		if (step == 0)
			throw new ValueError();
		return new SinglyGenerator<Double>() {
			private double current = first;
			public Double invoke() {
				if (step > 0 && current < last || step < 0 && current > last) {
					double result = current;
					current += step;
					return result;
				}
				throw stop;
			}
		};
	}
	
	public static <T> SinglyGenerator<T> constant(final T c) {
		return new SinglyGenerator<T>() {
			public T invoke() throws StopIteration {
				return c;
			}
		};
	}
	
	public static <T> SinglyGenerator<T> constant(final T c, final int n) {
		return new SinglyGenerator<T>() {
			private int i = 0;
			public T invoke() throws StopIteration {
				if (i++ < n)
					return c;
				throw stop;
			}
		};
	}
	
	public static <T> SinglyGenerator<T> make(final Lambda0<T> source) {
		return new SinglyGenerator<T>() {
			public T invoke() throws StopIteration {
				return source.invoke();
			}
		};
	}
	
	public static <T> SinglyGenerator<T> array(final Iterable<T> source) {
		return array(source.iterator());
	}
	
	public static <T> SinglyGenerator<T> array(final Iterator<T> iter) {
		return new SinglyGenerator<T>() {
			public T invoke() throws StopIteration {
				if (!iter.hasNext())
					throw stop;
				return iter.next();
			}
		};
	}
	
	@SafeVarargs
	public static <T> SinglyGenerator<T> array(final T... args) {
		return array(Arrays.asList(args));
	}
	
	@SafeVarargs
	public static <T> SinglyGenerator<T> chain(final SinglyGenerator<T>... args) {
		return chain(Generators.array(args));
	}
	
	public static <T> SinglyGenerator<T> chain(final Iterable<SinglyGenerator<T>> generators) {
		final SinglyGenerator<SinglyGenerator<T>> meta = Generators.array(generators);
		return new SinglyGenerator<T>() {
			private SinglyGenerator<T> current = null;
			public T invoke() throws StopIteration {
				if (current == null)
					current = meta.invoke();
				try {
					return current.invoke();
				} catch (StopIteration e) {
					current = meta.invoke();
					return invoke();
				}
			}
		};
	}
	
	public static <T> SinglyGenerator<T> single(final T value) {
		return constant(value).head(1);
	}
	
	public static <T> SinglyGenerator<T> empty() {
		return new SinglyGenerator<T>() {
			public T invoke() {
				throw stop;
			}
		};
	}
	
	public static <T> UndoableGenerator<T> uninvokableGenerator(final SinglyGenerator<T> it) {
		return new UndoableGenerator<T>(it);
	}
	
	public static <T, U> SinglyGenerator<Pair<T, U>> zip(final SinglyGenerator<T> first, final SinglyGenerator<U> second) {
		return new SinglyGenerator<Pair<T, U>>() {
			public Pair<T, U> invoke() throws StopIteration {
				return Pair.make(first.invoke(), second.invoke());
			}
		};
	}
	
	@SafeVarargs
	public static <T> SinglyGenerator<Tuple> tuple(final SinglyGenerator<T>... its) {
		return new SinglyGenerator<Tuple>() {
			public Tuple invoke() throws StopIteration {
				final Object[] result = new Object[its.length];
				for (int i = 0; i < its.length; ++i)
					result[i] = its[i].invoke();
				return Tuple.make(result);
			}
		};
	}
	
	public static <T> SinglyGenerator<List<T>> group(final SinglyGenerator<T> it, final int n) {
		return new SinglyGenerator<List<T>>() {
			public List<T> invoke() throws StopIteration {
				final List<T> result = new ArrayList<T>(n);
				try {
					for (int i = 0; i < n; ++i)
						result.add(it.invoke());
					return result;
				} catch (StopIteration e) {
					if (!result.isEmpty())
						return result;
					throw e;
				}
			}
		};
	}
	
	public static <T extends Comparable<? super T>> SinglyGenerator<T> sort(final SinglyGenerator<T> it) {
		return new SinglyGenerator<T>() {
			private Iterator<T> iter = null;
			public T invoke() throws StopIteration {
				if (iter == null) {
					List<T> array = it.toList();
					Collections.sort(array);
					iter = array.iterator();
				}
				if (!iter.hasNext()) {
					throw SinglyGenerator.stop;
				} else {
					return iter.next();
				}
			}
		};
	}
}
