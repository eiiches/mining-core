package jp.thisptr.math.distribution;

import java.util.Random;

public class UniformDistribution implements Distribution {
	private static final double EPSILON = 1e-10;
	
	private final double begin;
	private final double end;
	private final Random random = new Random();
	
	/**
	 * The uniform distribution in [begin, end].
	 * @param begin
	 * @param end
	 */
	public UniformDistribution(final double begin, final double end) {
		this.begin = begin;
		this.end = end;
	}

	@Override
	public double sample() {
		while (true) {
			final double n = begin + (end - begin + EPSILON) * random.nextDouble();
			if (n <= end)
				return n;
		}
	}
	
	@Override
	public double densityAt(final double x) {
		if (begin <= x && x <= end)
			return 1.0 / (end - begin);
		return 0;
	}
}
