package jp.thisptr.core.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import jp.thisptr.core.generator.signal.StopIteration;
import jp.thisptr.core.lambda.Lambda1;
import jp.thisptr.core.util.OutputParameter;

public abstract class ChunkedGenerator<T> extends AbstractGeneratorCore<List<T>> implements Generator<T> {
	
	public class GeneratorIterator implements Iterator<T> {
		private Iterator<T> iter = null;

		@Override
		public boolean hasNext() {
			if (iter == null || !iter.hasNext()) {
				try {
					iter = invoke().iterator();
					return hasNext();
				} catch (StopIteration e) {
					return false;
				}
			}
			return true;
		}

		@Override
		public T next() throws NoSuchElementException {
			if (!hasNext())
				throw new NoSuchElementException();
			return iter.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public Iterator<T> iterator() {
		return new GeneratorIterator();
	}
	
	public <U> ChunkedGenerator<U> map(final Lambda1<U, ? super T> f) {
		final ChunkedGenerator<T> it = this;
		return new ChunkedGenerator<U>() {
			public List<U> invoke() throws StopIteration {
				final List<U> result = new ArrayList<U>();
				for (T item : it.invoke())
					result.add(f.invoke(item));
				return result;
			}
		};
	}
	
	public <U> ChunkedGenerator<U> map(final Lambda1<U, ? super T> f, final OutputParameter<Future<?>> producerFuture, final ExecutorService mapExecutor, final int bufferSize) {
		final ChunkedGenerator<T> it = this;
		final Object poison = new Object();
		final BlockingQueue<Object> queue = (bufferSize > 0) ?
				new LinkedBlockingQueue<Object>(bufferSize) : new LinkedBlockingQueue<Object>();
		final ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
		final Runnable producer = new Runnable() {
			@Override
			public void run() {
				while (true) try {
					try {
						final List<T> items = it.invoke();
						Future<List<U>> resultFuture = mapExecutor.submit(new Callable<List<U>>() {
							public List<U> call() throws Exception {
								List<U> result = new ArrayList<U>(items.size());
								for (final T item : items)
									result.add(f.invoke(item));
								return result;
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
		return new ChunkedGenerator<U>() {
			public List<U> invoke() throws StopIteration {
				try {
					Object item = queue.take();
					if (item == poison)
						throw stop;
					@SuppressWarnings("unchecked")
					List<U> result = ((Future<List<U>>) item).get();
					return result;
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	public <U> U each(final Lambda1<U, ? super T> f) {
		U lastEvaluated = null;
		try {
			while (true)
				for (final T item : invoke())
					lastEvaluated = f.invoke(item);
		} catch (StopIteration e) { /* ignore */ }
		return lastEvaluated;
	}
	
	public T each() {
		T lastEvaluated = null;
		try {
			while (true)
				for (final T item : invoke())
					lastEvaluated = item;
		} catch (StopIteration e) { /* ignore */ }
		return lastEvaluated;
	}

	public ChunkedGenerator<T> tee(final Lambda1<?, ? super T> f) {
		final ChunkedGenerator<T> it = this;
		return new ChunkedGenerator<T>() {
			public List<T> invoke() throws StopIteration {
				final List<T> items = it.invoke();
				for (T item : items)
					f.invoke(item);
				return items;
			}
		};
	}
	
	public ChunkedGenerator<T> filterNull() {
		final ChunkedGenerator<T> it = this;
		return new ChunkedGenerator<T>() {
			public List<T> invoke() throws StopIteration {
				final List<T> items = it.invoke();
				final List<T> result = new ArrayList<T>(items.size());
				for (final T item : items)
					if (item != null)
						result.add(item);
				return result;
			}
		};
	}
	
	public List<T> toList() {
		final List<T> result = new ArrayList<T>();
		try {
			while (true)
				result.addAll(invoke());
		} catch (StopIteration e) { }
		return result;
	}
	
	/**
	 * Expand chunks.
	 */
	public SinglyGenerator<T> unchunk() {
		final ChunkedGenerator<T> it = this;
		return new SinglyGenerator<T>() {
			private Iterator<T> current = null;
			public T invoke() throws StopIteration {
				if (current == null || !current.hasNext()) {
					current = it.invoke().iterator();
					return invoke();
				}
				return current.next();
			}
		};
	}
	
	/**
	 * Merge all chunks and re-split into equally-sized chunks.
	 */
	public ChunkedGenerator<T> chunk(final int chunkSize) {
		return unchunk().chunk(chunkSize);
	}
}
