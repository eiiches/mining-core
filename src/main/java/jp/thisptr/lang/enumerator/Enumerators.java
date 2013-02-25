package jp.thisptr.lang.enumerator;

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

public final class Enumerators {
	private Enumerators() { }
	
	public static SinglyEnumerator<Integer> fibonatti() {
		return new SinglyEnumerator<Integer>() {
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
	
	public static SinglyEnumerator<Integer> sequence() {
		return new SinglyEnumerator<Integer>() {
			private int current = 0;
			public Integer invoke() throws StopIteration {
				return current++;
			}
		};
	}
	
	public static SinglyEnumerator<Integer> sequence(final int n) {
		return sequence(0, n);
	}
	
	public static SinglyEnumerator<Integer> sequence(final int first, final int last) {
		int step = last >= first ? 1 : -1;
		return sequence(first, last, step);
	}
	
	public static SinglyEnumerator<Integer> sequence(final int first, final int last, final int step) {
		if (step == 0)
			throw new ValueError();
		return new SinglyEnumerator<Integer>() {
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
	
	public static SinglyEnumerator<Double> sequence(final double first, final double last, final double step) {
		if (step == 0)
			throw new ValueError();
		return new SinglyEnumerator<Double>() {
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
	
	public static <T> SinglyEnumerator<T> constant(final T c) {
		return new SinglyEnumerator<T>() {
			public T invoke() throws StopIteration {
				return c;
			}
		};
	}
	
	public static <T> SinglyEnumerator<T> constant(final T c, final int n) {
		return new SinglyEnumerator<T>() {
			private int i = 0;
			public T invoke() throws StopIteration {
				if (i++ < n)
					return c;
				throw stop;
			}
		};
	}
	
	public static <T> SinglyEnumerator<T> make(final Lambda0<T> source) {
		return new SinglyEnumerator<T>() {
			public T invoke() throws StopIteration {
				return source.invoke();
			}
		};
	}
	
	public static <T> SinglyEnumerator<T> array(final Iterable<T> source) {
		return array(source.iterator());
	}
	
	public static <T> SinglyEnumerator<T> array(final Iterator<T> iter) {
		return new SinglyEnumerator<T>() {
			public T invoke() throws StopIteration {
				if (!iter.hasNext())
					throw stop;
				return iter.next();
			}
		};
	}
	
	@SafeVarargs
	public static <T> SinglyEnumerator<T> array(final T... args) {
		return array(Arrays.asList(args));
	}
	
	@SafeVarargs
	public static <T> SinglyEnumerator<T> chain(final SinglyEnumerator<T>... args) {
		return chain(Enumerators.array(args));
	}
	
	public static <T> SinglyEnumerator<T> chain(final Iterable<SinglyEnumerator<T>> generators) {
		final SinglyEnumerator<SinglyEnumerator<T>> meta = Enumerators.array(generators);
		return new SinglyEnumerator<T>() {
			private SinglyEnumerator<T> current = null;
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
	
	public static <T> SinglyEnumerator<T> single(final T value) {
		return constant(value).head(1);
	}
	
	public static <T> SinglyEnumerator<T> empty() {
		return new SinglyEnumerator<T>() {
			public T invoke() {
				throw stop;
			}
		};
	}
	
	public static <T> UndoableEnumerator<T> uninvokableGenerator(final SinglyEnumerator<T> it) {
		return new UndoableEnumerator<T>(it);
	}
	
	public static <T, U> SinglyEnumerator<Pair<T, U>> zip(final SinglyEnumerator<T> first, final SinglyEnumerator<U> second) {
		return new SinglyEnumerator<Pair<T, U>>() {
			public Pair<T, U> invoke() throws StopIteration {
				return Pair.make(first.invoke(), second.invoke());
			}
		};
	}
	
	@SafeVarargs
	public static <T> SinglyEnumerator<Tuple> tuple(final SinglyEnumerator<T>... its) {
		return new SinglyEnumerator<Tuple>() {
			public Tuple invoke() throws StopIteration {
				final Object[] result = new Object[its.length];
				for (int i = 0; i < its.length; ++i)
					result[i] = its[i].invoke();
				return Tuple.make(result);
			}
		};
	}
	
	public static <T> SinglyEnumerator<List<T>> group(final SinglyEnumerator<T> it, final int n) {
		return new SinglyEnumerator<List<T>>() {
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
	
	public static <T extends Comparable<? super T>> SinglyEnumerator<T> sort(final SinglyEnumerator<T> it) {
		return new SinglyEnumerator<T>() {
			private Iterator<T> iter = null;
			public T invoke() throws StopIteration {
				if (iter == null) {
					List<T> array = it.toList();
					Collections.sort(array);
					iter = array.iterator();
				}
				if (!iter.hasNext()) {
					throw SinglyEnumerator.stop;
				} else {
					return iter.next();
				}
			}
		};
	}
}
