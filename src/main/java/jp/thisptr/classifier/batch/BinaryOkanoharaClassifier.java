package jp.thisptr.classifier.batch;

import jp.thisptr.classifier.BatchLearner;
import jp.thisptr.classifier.instance.BasicInstances;

import org.apache.commons.lang.NotImplementedException;

public class BinaryOkanoharaClassifier implements BatchLearner<String, Boolean, BasicInstances<String, Boolean>> {
	@Override
	public void learn(final BasicInstances<String, Boolean> dataset) {
		throw new NotImplementedException();
	}
	
	@Override
	public Boolean classify(final String x) {
		throw new NotImplementedException();
	}
}