package jp.thisptr.lang.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import jp.thisptr.lang.ContinueIteration;
import jp.thisptr.lang.StopIteration;
import jp.thisptr.lang.TypeError;
import jp.thisptr.lang.UnsupportedTypeException;
import jp.thisptr.lang.ValueError;
import jp.thisptr.lang.lambda.Lambda1;
import jp.thisptr.lang.lambda.Lambda2;
import jp.thisptr.lang.tuple.Pair;
import jp.thisptr.util.Lambdas;
import jp.thisptr.util.OutputParameter;

public abstract class SinglyGenerator<T> extends AbstractGeneratorCore<T> implements Generator<T> {
	
	private class GeneratorIterator implements Iterator<T> {
		private T nextValue = null;
		private boolean nextFilled = false;
		private boolean finished = false;
		public boolean hasNext() {
			if (nextFilled)
				return true;
			try {
				nextValue = invoke();
				nextFilled = true;
				return true;
			} catch (StopIteration e) {
				finished = true;
				return false;
			}
		}
		public T next() {
			if (finished)
				throw new NoSuchElementException();
			if (!nextFilled)
				hasNext();
			nextFilled = false;
			return nextValue;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public Iterator<T> iterator() {
		return new GeneratorIterator();
	}
	
	public <U> U each(final Lambda1<U, ? super T> f) {
		U lastEvaluated = null;
		for (T item : this)
			lastEvaluated = f.invoke(item);
		return lastEvaluated;
	}
	
	public T each() {
		T lastEvaluated = null;
		for (T item : this)
			lastEvaluated = item;
		return lastEvaluated;
	}
	
	public List<T> toList() {
		List<T> result = new ArrayList<T>();
		try {
			while (true)
				result.add(invoke());
		} catch (StopIteration e) { /* ignore */ }
		return result;
	}

	public <U> SinglyGenerator<U> map(final Lambda1<U, ? super T> f, final OutputParameter<Future<?>> producerFuture, final ExecutorService mapExecutor) {
		return map(f, producerFuture, mapExecutor, -1);
	}
	
	public <U> SinglyGenerator<U> map(final Lambda1<U, ? super T> f, final OutputParameter<Future<?>> producerFuture, final ExecutorService mapExecutor, final int bufferSize) {
		final SinglyGenerator<T> original = this;
		final Object poison = new Object();
		final BlockingQueue<Object> queue = (bufferSize > 0) ?
				new LinkedBlockingQueue<Object>(bufferSize) : new LinkedBlockingQueue<Object>();
		final ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
		final Runnable producer = new Runnable() {
			@Override
			public void run() {
				while (true) try {
					try {
						final T item = original.invoke();
						Future<U> resultFuture = mapExecutor.submit(new Callable<U>() {
							public U call() throws Exception {
								return f.invoke(item);
							}
						});
						queue.put(resultFuture);
					} catch (StopIteration e) {
						queue.put(poison);
						break;
					}
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		};
		producerFuture.add(producerExecutor.submit(producer));
		producerExecutor.shutdown();
		return new SinglyGenerator<U>() {
			public U invoke() throws StopIteration {
				try {
					Object item = queue.take();
					if (item == poison)
						throw stop;
					@SuppressWarnings("unchecked")
					U result = ((Future<U>) item).get();
					return result;
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	public <U> SinglyGenerator<U> map(final Lambda1<U, ? super T> f) {
		final SinglyGenerator<T> original = this;
		return new SinglyGenerator<U>() {
			public U invoke() throws StopIteration {
				while (true) {
					try {
						return f.invoke(original.invoke());
					} catch (ContinueIteration e) {
						continue;
					}
				}
			}
		};
	}
	
	public SinglyGenerator<T> filter(final Lambda1<Boolean, ? super T> pred) {
		final SinglyGenerator<T> original = this;
		return new SinglyGenerator<T>() {
			public T invoke() throws StopIteration {
				while (true) {
					T item = original.invoke();
					if (pred.invoke(item))
						return item;
				}
			}
		};
	}
	
	public SinglyGenerator<T> head(final int n) {
		final SinglyGenerator<T> original = this;
		return new SinglyGenerator<T>() {
			private int i = 0;
			public T invoke() throws StopIteration {
				if (i++ >= n)
					throw stop;
				return original.invoke();
			}
		};
	}
	
	public SinglyGenerator<T> tail(final int n) {
		final LinkedList<T> queue = new LinkedList<T>();
		for (T item : this) {
			queue.add(item);
			while (queue.size() > n)
				queue.pop();
		}
		return Generators.array(queue);
	}
	
	public <U> SinglyGenerator<U> foldl(final Lambda2<U, ? super U, ? super T> f, final U initial) {
		final SinglyGenerator<T> it = this;
		return new SinglyGenerator<U>() {
			private U current;
			private boolean initialized = false;
			public U invoke() throws StopIteration {
				if (!initialized) {
					current = initial;
					initialized = true;
					return current;
				}
				current = f.invoke(current, it.invoke());
				return current;
			}
		};
	}
	
	public SinglyGenerator<T> foldl(final Lambda2<T, ? super T, ? super T> f) {
		final SinglyGenerator<T> it = this;
		return new SinglyGenerator<T>() {
			private T current;
			private boolean initialized = false;
			public T invoke() throws StopIteration {
				if (!initialized) {
					current = it.invoke();
					initialized = true;
					return current;
				}
				current = f.invoke(current, it.invoke());
				return current;
			}
		};
	}
	
	/**
	 * Almost equivalent to tail(1).invoke().
	 * @return The last value retrieved.
	 * @throws ValueError when no item was retrieved.
	 */
	public T eval() {
		try {
			return tail(1).invoke();
		} catch (StopIteration e) {
			throw new ValueError();
		}
	}
	
	public <U, IterableType extends Iterable<U>> SinglyGenerator<U> expand(final Lambda1<IterableType, ? super T> f) {
		final SinglyGenerator<T> original = this;
		return new SinglyGenerator<U>() {
			private Iterator<U> current = null;
			public U invoke() throws StopIteration {
				while (true) {
					if (current == null || !current.hasNext()) {
						try {
							current = f.invoke(original.invoke()).iterator();
						} catch (ContinueIteration e) { /* continue */ }
					} else {
						return current.next();
					}
				}
			}
		};
	}

	public <U, ListType extends List<U>> ChunkedGenerator<U> expandChunked(final Lambda1<ListType, ? super T> f) {
		final SinglyGenerator<T> it = this;
		return new ChunkedGenerator<U>() {
			public List<U> invoke() throws StopIteration {
				return f.invoke(it.invoke());
			}
		};
	}

	@Deprecated
	public <U> SinglyGenerator<U> ungroup(final Class<U> u) {
		final SinglyGenerator<?> it = this;
		return new SinglyGenerator<U>() {
			@SuppressWarnings("rawtypes")
			private Iterator current = null;
			public U invoke() throws StopIteration {
				while (true) {
					if (current == null || !current.hasNext()) {
						Object _current = it.invoke();
						if (!(_current instanceof Iterable))
							throw new UnsupportedTypeException();
						current = ((Iterable<?>) _current).iterator();
					} else {
						Object _item = current.next();
						if (!u.isInstance(_item))
							throw new TypeError();
						return u.cast(_item);
					}
				}
			}
		};
	}
	
	public SinglyGenerator<T> tee(final Lambda1<?, ? super T> f) {
		final SinglyGenerator<T> it = this;
		return new SinglyGenerator<T>() {
			public T invoke() throws StopIteration {
				final T item = it.invoke();
				f.invoke(item);
				return item;
			}
		};
	}
	
	public SinglyGenerator<List<T>> split(final int chunkSize) {
		final SinglyGenerator<T> it = this;
		return new SinglyGenerator<List<T>>() {
			public List<T> invoke() throws StopIteration {
				final List<T> result = new ArrayList<T>(chunkSize);
				try {
					for (int i = 0; i < chunkSize; ++i) 
						result.add(it.invoke());
					return result;
				} catch (StopIteration e) {
					if (result.isEmpty())
						throw e;
					return result;
				}
			}
		};
	}
	
	/**
	 * Unchunk a not chunked generator. This virtually does not do anything.
	 * @return this
	 */
	public SinglyGenerator<T> unchunk() {
		return this;
	}
	
	public ChunkedGenerator<T> chunk(final int chunkSize) {
		final SinglyGenerator<T> it = this;
		return new ChunkedGenerator<T>() {
			public List<T> invoke() throws StopIteration {
				final List<T> result = new ArrayList<T>(chunkSize);
				try {
					for (int i = 0; i < chunkSize; ++i) 
						result.add(it.invoke());
					return result;
				} catch (StopIteration e) {
					if (result.isEmpty())
						throw e;
					return result;
				}
			}
		};
	}
	
	public SinglyGenerator<T> buffer(final OutputParameter<Future<?>> future) {
		return buffer(future, -1);
	}
	
	public SinglyGenerator<T> buffer(final OutputParameter<Future<?>> future, final int n) {
		final Object poison = new Object();
		final BlockingQueue<Object> queue = (n > 0) ? new LinkedBlockingQueue<Object>(n) : new LinkedBlockingQueue<Object>();
		final SinglyGenerator<T> original = this;
		Runnable producer = new Runnable() {
			public void run() {
				while (true) try {
					try {
						queue.put(original.invoke());
					} catch (StopIteration e) {
						queue.put(poison);
						break;
					}
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		};
		ExecutorService executor = Executors.newSingleThreadExecutor();
		future.add(executor.submit(producer));
		executor.shutdown(); // automatically shutdown thread upon completion.
		return new SinglyGenerator<T>() {
			public T invoke() throws StopIteration {
				try {
					Object item = queue.take();
					if (item == poison)
						throw stop;
					@SuppressWarnings("unchecked")
					T result = (T) item;
					return result;
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	public <U> SinglyGenerator<List<T>> group(final Lambda1<U, ? super T> f) {
		final UninvokableGenerator<T> it = Generators.uninvokableGenerator(this);
		return new SinglyGenerator<List<T>>() {
			public List<T> invoke() throws StopIteration {
				List<T> result = new ArrayList<T>();
				U key = null;
				try {
					while (true) {
						final T current = it.invoke();
						final U currentKey = f.invoke(current);
						if (!result.isEmpty() && !key.equals(currentKey)) {
							it.uninvoke(current);
							return result;
						}
						key = currentKey;
						result.add(current);
					}
				} catch (StopIteration e) {
					if (result.isEmpty())
						throw e;
					return result;
				}
			}
			
		};
	}
	
	public <U> SinglyGenerator<Pair<T, U>> zip(final SinglyGenerator<U> it) {
		return Generators.zip(this, it);
	}
	
	public SinglyGenerator<List<T>> group(final int n) {
		return Generators.group(this, n);
	}
	
	public SinglyGenerator<List<T>> group() {
		return group(Lambdas.<T>equals());
	}
	
	public SinglyGenerator<List<T>> group(final Lambda2<Boolean, ? super T, ? super T> isGroup) {
		final UninvokableGenerator<T> it = Generators.uninvokableGenerator(this);
		return new SinglyGenerator<List<T>>() {
			public List<T> invoke() throws StopIteration {
				final List<T> result = new ArrayList<T>();
				try {
					while (true) {
						T current = it.invoke();
						if (!result.isEmpty() && !isGroup.invoke(current, result.get(result.size() - 1))) {
							it.uninvoke(current);
							return result;
						}
						result.add(current);
					}
				} catch (StopIteration e) {
					if (result.isEmpty())
						throw e;
					return result;
				}
			}
		};
	}
	
	public SinglyGenerator<List<T>> window(final int n) {
		final SinglyGenerator<T> it = this;
		return new SinglyGenerator<List<T>>() {
			private LinkedList<T> queue = new LinkedList<T>();
			public List<T> invoke() throws StopIteration {
				if (!queue.isEmpty())
					queue.removeFirst();
				while (queue.size() < n)
					queue.addLast(it.invoke());
				return new ArrayList<T>(queue);
			}
		};
	}
	
	public SinglyGenerator<T> chain(final SinglyGenerator<T> generator) {
		return Generators.chain(this, generator);
	}
	
	public SinglyGenerator<T> chain(final Iterable<T> iterable) {
		return Generators.chain(this, Generators.array(iterable));
	}
	
	public SinglyGenerator<T> unique() {
		final SinglyGenerator<T> it = this;
		return new SinglyGenerator<T>() {
			private T prev = null;
			public T invoke() throws StopIteration {
				while (true) {
					T item = it.invoke();
					if (prev != null && prev.equals(item))
						continue;
					prev = item;
					return item;
				}
			}
		};
	}

	public SinglyGenerator<T> filterNull() {
		return filter(new Lambda1<Boolean, T>() {
			public Boolean invoke(final T item) {
				return item != null;
			}
		});
	}
}