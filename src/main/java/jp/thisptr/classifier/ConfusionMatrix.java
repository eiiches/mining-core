package jp.thisptr.classifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfusionMatrix<CategoryType> {
	private Set<CategoryType> categories;
	
	/* Map<actual, Map<predicted, count>> */
	private Map<CategoryType, Map<CategoryType, Integer>> matrix;
	
	public ConfusionMatrix() {
		categories = new HashSet<CategoryType>();
		matrix = new HashMap<CategoryType, Map<CategoryType, Integer>>();
	}
	
	public void add(final CategoryType expected, final CategoryType predicted) {
		add(expected, predicted, 1);
	}
	
	public void add(final CategoryType expected, final CategoryType predicted, final int inc) {
		categories.add(expected);
		categories.add(predicted);
		
		Map<CategoryType, Integer> inner = matrix.get(expected);
		if (inner == null) {
			inner = new HashMap<CategoryType, Integer>();
			matrix.put(expected, inner);
		}
		
		Integer count = inner.get(predicted);
		if (count == null) {
			count = Integer.valueOf(0);
		}
		inner.put(predicted, count + inc);
	}
	
	public int getCount(final CategoryType actual, final CategoryType predicted) {
		Map<CategoryType, Integer> inner = matrix.get(actual);
		if (inner == null)
			return 0;
		Integer value = inner.get(predicted);
		if (value == null)
			return 0;
		return value;
	}
	
	public int getCountPredicted(final CategoryType predicted) {
		int result = 0;
		for (final Map<CategoryType, Integer> inner : matrix.values()) {
			Integer value = inner.get(predicted);
			if (value == null)
				continue;
			result += value;
		}
		return result;
	}
	
	public int getCountActual(final CategoryType actual) {
		final Map<CategoryType, Integer> inner = matrix.get(actual);
		if (inner == null)
			return 0;
		int result = 0;
		for (final Integer value : inner.values())
			result += value;
		return result;
	}
	
	public double getPrecision(final CategoryType category) {
		return getCount(category, category) / (double) getCountPredicted(category);
	}
	
	public double getRecall(final CategoryType category) {
		return getCount(category, category) / (double) getCountActual(category);
	}
	
	public double getFMeasure(final CategoryType category) {
		double precision = getPrecision(category);
		double recall = getRecall(category);
		return 2 * precision * recall / (precision + recall);
	}

	public double getAccuracy() {
		int all = 0;
		int correct = 0;
		for (final Map.Entry<CategoryType, Map<CategoryType, Integer>> entry : matrix.entrySet()) {
			for (final Map.Entry<CategoryType, Integer> innerEntry : entry.getValue().entrySet()) {
				if (entry.getKey().equals(innerEntry.getKey()))
					correct += innerEntry.getValue();
				all += innerEntry.getValue();
			}
		}
		return correct / (double) all;
	}
	
	public String toPrettyString() {
		StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		for (CategoryType actual : categories) {
			if (!isFirst)
				builder.append("\n");
			builder.append("[");
			boolean isFirst2 = true; // FIXME: complicated. should use join stead.
			for (CategoryType predicted : categories) {
				if (!isFirst2)
					builder.append(",");
				// FIXME: adjust width according to value
				builder.append(String.format("%5d", getCount(actual, predicted)));
				isFirst2 = false;
			}
			builder.append("]");
			isFirst = false;
		}
		return builder.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		boolean isFirst = true;
		for (CategoryType actual : categories) {
			if (!isFirst)
				builder.append(",");
			builder.append("[");
			boolean isFirst2 = true; // FIXME: complicated. should use join stead.
			for (CategoryType predicted : categories) {
				if (!isFirst2)
					builder.append(",");
				builder.append(String.format("%d", getCount(actual, predicted)));
				isFirst2 = false;
			}
			builder.append("]");
			isFirst = false;
		}
		builder.append("]");
		return builder.toString();
	}

	public void add(final ConfusionMatrix<CategoryType> cm) {
		for (Map.Entry<CategoryType, Map<CategoryType, Integer>> entry : cm.matrix.entrySet())
			for (Map.Entry<CategoryType, Integer> innerEntry : entry.getValue().entrySet()) {
				add(entry.getKey(), innerEntry.getKey(), innerEntry.getValue());
			}
	}
}