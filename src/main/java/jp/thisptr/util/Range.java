package jp.thisptr.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable range class representing a [begin, end) interval.
 */
public class Range {
	private final int begin;
	private final int end;
	public Range(final int begin, final int end) {
		this.begin = begin;
		this.end = end;
	}
	public List<Range> split(final int blockSize) {
		final List<Range> result = new ArrayList<Range>();
		int first = this.begin;
		while (true) {
			if (first + blockSize >= end) {
				result.add(new Range(first, end));
				break;
			}
			result.add(new Range(first, first + blockSize));
			first += blockSize;
		}
		return result;
	}
	public int begin() {
		return begin;
	}
	public int end() {
		return end;
	}
}