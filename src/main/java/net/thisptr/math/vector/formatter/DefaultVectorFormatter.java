package net.thisptr.math.vector.formatter;

import net.thisptr.math.vector.Vector;

public class DefaultVectorFormatter implements VectorFormatter {
	private boolean sparseOutput;
	private boolean colorEnabled;
	private double min;
	private double max;
	private int precision;
	private boolean bold;

	public DefaultVectorFormatter setColorEnabled(boolean colorEnabled) {
		this.colorEnabled = colorEnabled;
		return this;
	}

	public DefaultVectorFormatter setSparseOutput(boolean sparseOutput) {
		this.sparseOutput = sparseOutput;
		return this;
	}

	public DefaultVectorFormatter setColorRange(final double min, final double max) {
		this.min = min;
		this.max = max;
		this.colorEnabled = true;
		return this;
	}

	public DefaultVectorFormatter setPrecision(final int precision) {
		this.precision = precision;
		return this;
	}

	public String format(final double v) {
		if (!colorEnabled)
			return String.format("%10f", v);
		int index = (int) ((v - min) / ((max - min) / COLORS.length));
		if (index < 0)
			index = 0;
		if (COLORS.length <= index)
			index = COLORS.length - 1;
		return String.format("\033[%s%dm%10f\033[0m", bold ? "1;" : "", COLORS[index], v);
	}

	public static int[] COLORS = new int[] {
		31,
		36,
	};

	@Override
	public String format(final Vector v) {
		final StringBuilder builder = new StringBuilder("[");
		if (sparseOutput) {
			v.walk(new Vector.Visitor() {
				private boolean isFirst = true;
				@Override
				public void visit(final int index, final double value) {
					if (!isFirst) {
						builder.append(", ");
						isFirst = false;
					}
					builder.append(String.format("%3s: %s", index, format(value)));
				}
			});
		} else {
			for (int i = 0; i < v.size(); ++i) {
				if (i != 0)
					builder.append(", ");
				builder.append(format(v.get(i)));
			}
		}
		builder.append("]");
		return builder.toString();
	}

	public void setBold(final boolean bold) {
		this.bold = bold;
	}
}