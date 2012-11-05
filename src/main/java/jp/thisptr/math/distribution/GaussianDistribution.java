package jp.thisptr.math.distribution;

import java.util.Random;

public class GaussianDistribution extends Distribution {
	private final double average;
	private final double deviation;
	private final Random random = new Random();
	
	public GaussianDistribution() {
		this(0.0, 1.0);
	}
	
	public GaussianDistribution(final double average, final double deviation) {
		this.average = average;
		this.deviation = deviation;
	}

	@Override
	public double sample() {
		return random.nextGaussian() * deviation + average;
	}
}