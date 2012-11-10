package jp.thisptr.classifier.instance.transform;

import jp.thisptr.classifier.instance.BasicInstances;

public abstract class BasicTransformer<VectorType, InstancesType extends BasicInstances<VectorType, ?>> {
	public abstract void transform(final InstancesType instances);
}