package net.thisptr.neuralnet;

import java.util.List;

import net.thisptr.math.vector.Vector;

/**
 *　教師なしのオンライン学習を行うが、複数の学習データをまとめて行うことで高速化が
 * 期待できる学習器に実装される。
 */
public interface UnsupervisedOnlineBatchLearner {
	public void train(final List<Vector> vector);
}
