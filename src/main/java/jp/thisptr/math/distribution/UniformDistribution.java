package jp.thisptr.math.distribution;

import java.util.Random;

public class UniformDistribution implements Distribution {
	private final double begin;
	private final double end;
	private final Random random = new Random();
	
	/**
	 * The uniform distribution in [begin, end).
	 * @param begin
	 * @param end
	 */
	public UniformDistribution(final double begin, final double end) {
		this.begin = begin;
		this.end = end;
	}

	@Override
	public double sample() {
		return begin + (end - begin) * random.nextDouble();
	}
	
	@Override
	public double at(final double x) {
		if (begin <= x && x < end)
			return 1.0 / (end - begin);
		return 0;
	}
}
