package jp.thisptr.classifier.instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BasicInstances<VectorType, CategoryType> implements Iterable<Instance<VectorType, CategoryType>> {
	private final List<Instance<VectorType, CategoryType>> instances;
	
	public BasicInstances() {
		this.instances = new ArrayList<Instance<VectorType, CategoryType>>();
	}
	
	public BasicInstances(final BasicInstances<VectorType, CategoryType> instances) {
		this.instances = new ArrayList<Instance<VectorType, CategoryType>>(instances.instances);
	}
	
	public BasicInstances(final Iterable<Instance<VectorType, CategoryType>> instances) {
		this.instances = new ArrayList<Instance<VectorType, CategoryType>>();
		for (final Instance<VectorType, CategoryType> instance : instances)
			this.instances.add(instance);
	}
	
	public int size() {
		return instances.size();
	}
	
	public void add(final Instance<VectorType, CategoryType> instance) {
		instances.add(instance);
	}
	
	public void add(final VectorType vector, final CategoryType label) {
		add(new Instance<VectorType, CategoryType>(vector, label));
	}
	
	public Instance<VectorType, CategoryType> get(final int index) {
		return instances.get(index);
	}

	public Iterator<Instance<VectorType, CategoryType>> iterator() {
		return instances.iterator();
	}
	
	public void shuffle() {
		Collections.shuffle(instances);
	}
}
