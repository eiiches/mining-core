package jp.thisptr.core.lambda.util;

import jp.thisptr.core.lambda.alias.Predicate;

public final class Predicates {
	private Predicates() { }
	
	public static Predicate<Integer> isOdd() {
		return new IsOdd();
	}
	public static class IsOdd extends Predicate<Integer> {
		public Boolean invoke(final Integer arg1) {
			return (arg1 & 0x1) == 1;
		}
	}
	public static Predicate<Integer> isEven() {
		return new IsEven();
	}
	public static class IsEven extends Predicate<Integer> {
		public Boolean invoke(final Integer arg1) {
			return (arg1 & 0x1) == 0;
		}
	}
}
