package net.thisptr.lang.enumerator;

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

import net.thisptr.lang.ContinueIteration;
import net.thisptr.lang.StopIteration;
import net.thisptr.lang.TypeError;
import net.thisptr.lang.UnsupportedTypeException;
import net.thisptr.lang.ValueError;
import net.thisptr.lang.lambda.Lambda1;
import net.thisptr.lang.lambda.Lambda2;
import net.thisptr.lang.tuple.Pair;
import net.thisptr.util.Lambdas;
import net.thisptr.util.OutputParameter;

public abstract class SinglyEnumerator<T> extends AbstractEnumeratorCore<T> implements Enumerator<T> {
	
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

	public <U> SinglyEnumerator<U> map(final Lambda1<U, ? super T> f, final OutputParameter<Future<?>> producerFuture, final ExecutorService mapExecutor) {
		return map(f, producerFuture, mapExecutor, -1);
	}
	
	public <U> SinglyEnumerator<U> map(final Lambda1<U, ? super T> f, final OutputParameter<Future<?>> producerFuture, final ExecutorService mapExecutor, final int bufferSize) {
		final SinglyEnumerator<T> original = this;
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
		return new SinglyEnumerator<U>() {
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
	
	public <U> SinglyEnumerator<U> map(final Lambda1<U, ? super T> f) {
		final SinglyEnumerator<T> original = this;
		return new SinglyEnumerator<U>() {
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
	
	public SinglyEnumerator<T> filter(final Lambda1<Boolean, ? super T> pred) {
		final SinglyEnumerator<T> original = this;
		return new SinglyEnumerator<T>() {
			public T invoke() throws StopIteration {
				while (true) {
					T item = original.invoke();
					if (pred.invoke(item))
						return item;
				}
			}
		};
	}
	
	public SinglyEnumerator<T> head(final int n) {
		final SinglyEnumerator<T> original = this;
		return new SinglyEnumerator<T>() {
			private int i = 0;
			public T invoke() throws StopIteration {
				if (i++ >= n)
					throw stop;
				return original.invoke();
			}
		};
	}
	
	public SinglyEnumerator<T> tail(final int n) {
		final LinkedList<T> queue = new LinkedList<T>();
		for (T item : this) {
			queue.add(item);
			while (queue.size() > n)
				queue.pop();
		}
		return Enumerators.array(queue);
	}
	
	public <U> SinglyEnumerator<U> foldl(final Lambda2<U, ? super U, ? super T> f, final U initial) {
		final SinglyEnumerator<T> it = this;
		return new SinglyEnumerator<U>() {
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
	
	public SinglyEnumerator<T> foldl(final Lambda2<T, ? super T, ? super T> f) {
		final SinglyEnumerator<T> it = this;
		return new SinglyEnumerator<T>() {
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
	
	public <U, IterableType extends Iterable<U>> SinglyEnumerator<U> expand(final Lambda1<IterableType, ? super T> f) {
		final SinglyEnumerator<T> original = this;
		return new SinglyEnumerator<U>() {
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

	public <U, ListType extends List<U>> ChunkedEnumerator<U> expandChunked(final Lambda1<ListType, ? super T> f) {
		final SinglyEnumerator<T> it = this;
		return new ChunkedEnumerator<U>() {
			public List<U> invoke() throws StopIteration {
				return f.invoke(it.invoke());
			}
		};
	}

	@Deprecated
	public <U> SinglyEnumerator<U> ungroup(final Class<U> u) {
		final SinglyEnumerator<?> it = this;
		return new SinglyEnumerator<U>() {
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
	
	public SinglyEnumerator<T> tee(final Lambda1<?, ? super T> f) {
		final SinglyEnumerator<T> it = this;
		return new SinglyEnumerator<T>() {
			public T invoke() throws StopIteration {
				final T item = it.invoke();
				f.invoke(item);
				return item;
			}
		};
	}
	
	public SinglyEnumerator<List<T>> split(final int chunkSize) {
		final SinglyEnumerator<T> it = this;
		return new SinglyEnumerator<List<T>>() {
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
	public SinglyEnumerator<T> unchunk() {
		return this;
	}
	
	public ChunkedEnumerator<T> chunk(final int chunkSize) {
		final SinglyEnumerator<T> it = this;
		return new ChunkedEnumerator<T>() {
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
	
	public SinglyEnumerator<T> buffer(final OutputParameter<Future<?>> future) {
		return buffer(future, -1);
	}
	
	public SinglyEnumerator<T> buffer(final OutputParameter<Future<?>> future, final int n) {
		final Object poison = new Object();
		final BlockingQueue<Object> queue = (n > 0) ? new LinkedBlockingQueue<Object>(n) : new LinkedBlockingQueue<Object>();
		final SinglyEnumerator<T> original = this;
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
		return new SinglyEnumerator<T>() {
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
	
	public <U> SinglyEnumerator<List<T>> group(final Lambda1<U, ? super T> f) {
		final UndoableEnumerator<T> it = Enumerators.uninvokableGenerator(this);
		return new SinglyEnumerator<List<T>>() {
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
	
	public <U> SinglyEnumerator<Pair<T, U>> zip(final SinglyEnumerator<U> it) {
		return Enumerators.zip(this, it);
	}
	
	public SinglyEnumerator<List<T>> group(final int n) {
		return Enumerators.group(this, n);
	}
	
	public SinglyEnumerator<List<T>> group() {
		return group(Lambdas.<T>equals());
	}
	
	public SinglyEnumerator<List<T>> group(final Lambda2<Boolean, ? super T, ? super T> isGroup) {
		final UndoableEnumerator<T> it = Enumerators.uninvokableGenerator(this);
		return new SinglyEnumerator<List<T>>() {
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
	
	public SinglyEnumerator<List<T>> window(final int n) {
		final SinglyEnumerator<T> it = this;
		return new SinglyEnumerator<List<T>>() {
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
	
	public SinglyEnumerator<T> chain(final SinglyEnumerator<T> generator) {
		return Enumerators.chain(this, generator);
	}
	
	public SinglyEnumerator<T> chain(final Iterable<T> iterable) {
		return Enumerators.chain(this, Enumerators.array(iterable));
	}
	
	public SinglyEnumerator<T> unique() {
		final SinglyEnumerator<T> it = this;
		return new SinglyEnumerator<T>() {
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

	public SinglyEnumerator<T> filterNull() {
		return filter(new Lambda1<Boolean, T>() {
			public Boolean invoke(final T item) {
				return item != null;
			}
		});
	}
	
	public SinglyEnumerator<T> skip(final int n) {
		final SinglyEnumerator<T> it = this;
		return new SinglyEnumerator<T>() {
			private boolean isFirstInvocation = true;
			public T invoke() throws StopIteration {
				if (isFirstInvocation) {
					for (int i = 0; i < n; ++i)
						it.invoke();
					isFirstInvocation = false;
				}
				return it.invoke();
			}
		};
	}
}