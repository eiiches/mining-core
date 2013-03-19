package net.thisptr.util;

import java.util.Comparator;

import net.thisptr.lang.lambda.Lambda1;
import net.thisptr.lang.tuple.Pair;

public final class PairUtils {
	private PairUtils() { }
	
	public static <T, U> GetFirst<T, U> getFirst() {
		return new GetFirst<T, U>();
	}
	
	public static <T, U> GetSecond<T, U> getSecond() {
		return new GetSecond<T, U>();
	}
	
	public static class GetFirst<T, U> extends Lambda1<T, Pair<T, U>> {
		public T invoke(final Pair<T, U> p) {
			return p.getFirst();
		}
	}
	
	public static class GetSecond<T, U> extends Lambda1<U, Pair<T, U>> {
		public U invoke(final Pair<T, U> p) {
			return p.getSecond();
		}
	}
	
	public static class SecondComparator<T extends Comparable<T>> implements Comparator<Pair<?, T>> {
		@Override
		public int compare(final Pair<?, T> o1, final Pair<?, T> o2) {
			return o1.getSecond().compareTo(o2.getSecond());
		}
	}
	
	public static class FirstComparator<T extends Comparable<T>> implements Comparator<Pair<T, ?>> {
		@Override
		public int compare(final Pair<T, ?> o1, final Pair<T, ?> o2) {
			return o1.getFirst().compareTo(o1.getFirst());
		}
	}
}
