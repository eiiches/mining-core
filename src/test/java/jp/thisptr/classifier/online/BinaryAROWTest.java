package jp.thisptr.classifier.online;

import jp.thisptr.classifier.OnlineLearner;
import jp.thisptr.math.structure.vector.SparseMapVector;

// FIXME: Extending BinaryPerceptronTest is a bad idea.
public class BinaryAROWTest extends BinaryPerceptronTest {
	@Override
	public OnlineLearner<SparseMapVector, Boolean> doCreateLearner() {
		return new BinaryAROW(0.1, 10);
	}
}
