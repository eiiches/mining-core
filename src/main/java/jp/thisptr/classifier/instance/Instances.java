package jp.thisptr.classifier.instance;

import jp.thisptr.math.structure.vector.Vector;

public class Instances<VectorType extends Vector, CategoryType> extends BasicInstances<VectorType, CategoryType> {
	private int dim = 0;
	
	public Instances() {
	}
	
	public Instances(final BasicInstances<VectorType, CategoryType> instances) {
		for (final Instance<VectorType, CategoryType> instance : instances)
			add(instance);
	}
	
	public int dim() {
		return dim;
	}
	
	public void add(final Instance<VectorType, CategoryType> instance) {
		super.add(instance);
		dim = Math.max(instance.getVector().size(), dim);
	}
}
