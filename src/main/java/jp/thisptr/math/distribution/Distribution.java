package jp.thisptr.math.distribution;

public interface Distribution {
	double sample();
	double at(final double x);
}
