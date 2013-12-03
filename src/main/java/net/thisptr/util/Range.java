package net.thisptr.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable range class representing a [begin, end) interval.
 */
public class Range {
	private final int begin;
	private final int end;

	public Range(final int begin, final int end) {
		if (begin > end)
			throw new IllegalArgumentException("begin must smaller or equal to end");
		this.begin = begin;
		this.end = end;
	}

	public List<Range> slice(final int sliceSize) {
		final List<Range> result = new ArrayList<Range>();
		int first = this.begin;
		while (true) {
			if (first + sliceSize >= end) {
				final Range slice = new Range(first, end);
				if (!slice.isEmpty())
					result.add(slice);
				break;
			}
			result.add(new Range(first, first + sliceSize));
			first += sliceSize;
		}
		return result;
	}
	
	public List<Range> split(final int n) {
		final List<Range> result = new ArrayList<Range>();
		final double sliceSize = size() / (double) n;

		double begin = this.begin;
		while (begin < this.end) {
			final Range split = new Range((int) Math.ceil(begin), (int) Math.ceil(begin + sliceSize));
			if (!split.isEmpty())
				result.add(split);
			begin += sliceSize;
		}

		return result;
	}

	public int begin() {
		return begin;
	}

	public int end() {
		return end;
	}
	
	public int size() {
		return end - begin;
	}
	
	public boolean isEmpty() {
		return end == begin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + begin;
		result = prime * result + end;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Range other = (Range) obj;
		if (begin != other.begin)
			return false;
		if (end != other.end)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("[%d, %d)", begin, end);
	}
}