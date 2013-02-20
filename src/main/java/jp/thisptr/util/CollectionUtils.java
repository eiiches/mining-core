package jp.thisptr.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import jp.thisptr.lang.ContinueIteration;
import jp.thisptr.lang.ValueError;
import jp.thisptr.lang.collection.DefaultMap;
import jp.thisptr.lang.generator.Generators;
import jp.thisptr.lang.lambda.Lambda0;
import jp.thisptr.lang.lambda.Lambda1;
import jp.thisptr.lang.lambda.Lambda2;
import jp.thisptr.lang.lambda.alias.Predicate;
import jp.thisptr.lang.tuple.Pair;

public final class CollectionUtils {
	private CollectionUtils() { }
	
	public static <T, U> U foldl(final Iterable<T> iterable, final Lambda2<U, U, T> f, final U initial) {
		U result = initial;
		for (T item : iterable) {
			try {
				result = f.invoke(result, item);
			} catch (ContinueIteration e) {
				continue;
			}
		}
		return result;
	}
	
	public static <T> T foldl(final Iterable<T> iterable, final Lambda2<T, T, T> f) {
		Iterator<T> iter = iterable.iterator();
		if (!iter.hasNext())
			throw new ValueError();
		return foldl(Generators.array(iter), f, iter.next());
	}
	
	public static <T extends Number> T sum(final Iterable<T> iterable) {
		return foldl(iterable, Lambdas.<T>add());
	}
	
	public static <T, U> List<U> map(final Iterable<T> iterable, final Lambda1<U, T> f) {
		List<U> result = new ArrayList<U>();
		for (T item : iterable) {
			try {
				result.add(f.invoke(item));
			} catch (ContinueIteration e) {
				continue;
			}
		}
		return result;
	}
	
	public static <T, U> List<U> map(final T[] iterable, final Lambda1<U, T> f) {
		return CollectionUtils.map(Arrays.asList(iterable), f);
	}

	public static <T> List<T> filter(final Iterable<T> iterable, final Predicate<T> f) {
		List<T> result = new ArrayList<T>();
		for (T item : iterable) {
			try {
				if (f.invoke(item))
					result.add(item);
			} catch (ContinueIteration e) {
				continue;
			}
		}
		return result;
	}
	
	public static <Item, Score extends Comparable<? super Score>> Pair<Item, Score> maximize(final Iterable<Item> items, final Lambda1<Score, Item> scorefunc) {
		Score bestScore = null;
		Item bestItem = null;
		for (Item item : items) {
			Score score = scorefunc.invoke(item);
			if (bestScore == null || score.compareTo(bestScore) > 0) {
				bestScore = score;
				bestItem = item;
			}
		}
		return Pair.make(bestItem, bestScore);
	}
	
	public static <Item, Score> Pair<Item, Score> maximize(final Iterable<Item> items, final Lambda1<Score, Item> scorefunc, final Comparator<Score> comparator) {
		Score bestScore = null;
		Item bestItem = null;
		for (Item item : items) {
			Score score = scorefunc.invoke(item);
			if (bestScore == null || comparator.compare(score, bestScore) > 0) {
				bestScore = score;
				bestItem = item;
			}
		}
		return Pair.make(bestItem, bestScore);
	}
	
	public static <Item, Score> Pair<Item, Score> maximize(final Iterable<Item> items, final Lambda1<Score, Item> scorefunc, final Comparator<Score> comparator, final ExecutorService executor) {
		final List<Future<Pair<Item, Score>>> itemScoreFutures = new ArrayList<Future<Pair<Item, Score>>>();
		for (final Item item : items) {
			itemScoreFutures.add(executor.submit(new Callable<Pair<Item, Score>>() {
				public Pair<Item, Score> call() throws Exception {
					return Pair.make(item, scorefunc.invoke(item));
				}
			}));
		}
		Pair<Item, Score> best = null;
		for (final Future<Pair<Item, Score>> itemScoreFuture : itemScoreFutures) {
			try {
				final Pair<Item, Score> itemScore = itemScoreFuture.get();
				if (best == null || comparator.compare(itemScore.getSecond(), best.getSecond()) > 0)
					best = itemScore;
			} catch (ExecutionException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		return best;
	}
	
	public static <Item, Score> Pair<Item, Score> maximize(final Iterable<Item> items, final Lambda1<Score, Item> scorefunc, final Comparator<Score> comparator, final int nThreads) {
		final ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		final Pair<Item, Score> result = maximize(items, scorefunc, comparator, executor);
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public static <T> T max(final Iterable<T> iterable, final Comparator<T> comparator) {
		T bestItem = null;
		for (T item : iterable) {
			if (bestItem == null || comparator.compare(item, bestItem) > 0)
				bestItem = item;
		}
		return bestItem;
	}
	
	public static <T extends Comparable<T>> T max(final Iterable<T> iterable) {
		T bestItem = null;
		for (T item : iterable) {
			if (bestItem == null || item.compareTo(bestItem) > 0)
				bestItem = item;
		}
		return bestItem;
	}
}
