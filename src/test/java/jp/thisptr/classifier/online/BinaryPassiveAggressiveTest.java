package jp.thisptr.classifier.online;

import jp.thisptr.classifier.OnlineLearner;
import jp.thisptr.math.structure.vector.SparseMapVector;

// FIXME: Extending BinaryPerceptronTest is a bad idea.
public class BinaryPassiveAggressiveTest extends BinaryPerceptronTest {
	@Override
	public OnlineLearner<SparseMapVector, Boolean> doCreateLearner() {
		// TODO: We have to test Mode.PA_I and Mode.PA_II
		return new BinaryPassiveAggressive(BinaryPassiveAggressive.Mode.PA_I);
	}
}
