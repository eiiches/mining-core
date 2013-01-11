package jp.thisptr.classifier.batch;

import java.util.List;

import jp.thisptr.classifier.BatchLearner;
import jp.thisptr.instance.LabeledInstance;

import org.apache.commons.lang.NotImplementedException;

public class BinaryOkanoharaClassifier implements BatchLearner<String, Boolean> {
	@Override
	public Boolean classify(final String x) {
		throw new NotImplementedException();
	}

	@Override
	public void learn(final List<? extends LabeledInstance<? extends String, Boolean>> instances) {
		throw new NotImplementedException();
	}
}