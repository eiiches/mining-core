package jp.thisptr.classifier.online;

import jp.thisptr.classifier.OnlineLearner;
import jp.thisptr.math.structure.vector.SparseMapVector;

// FIXME: Extending BinaryPerceptronTest is a bad idea.
public class BinarySCWTest extends BinaryPerceptronTest {
	@Override
	public OnlineLearner<SparseMapVector, Boolean> doCreateLearner() {
		return new BinarySCW(0.2, 0.5);
	}
}
