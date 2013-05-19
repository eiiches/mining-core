package net.thisptr.util;


public class ScoredItem<T> implements Comparable<ScoredItem<T>> {
	private final double score;
	private final T item;
	
	public ScoredItem(final T item, final double score) {
		this.score = score;
		this.item = item;
	}
	
	public double score() {
		return this.score;
	}
	
	public T item() {
		return this.item;
	}

	@Override
	public int compareTo(ScoredItem<T> o) {
		// higher scores should come first when sorted.
		return -Double.valueOf(score).compareTo(o.score);
	}
}