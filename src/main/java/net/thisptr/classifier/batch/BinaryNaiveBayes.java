package net.thisptr.classifier.batch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thisptr.classifier.BatchLearner;
import net.thisptr.instance.Instances;
import net.thisptr.instance.LabeledInstance;
import net.thisptr.math.vector.Vector;

public class BinaryNaiveBayes implements BatchLearner<Vector, Boolean> {
	
	public static final double DEFAULT_SMOOTHING_VALUE = 0.1;
	
	private double smoothingValue = DEFAULT_SMOOTHING_VALUE;
	
	private static class ClassCount {
		private double sum;
		private double[] values;
		public double getSum() {
			return sum;
		}
		public double[] getValues() {
			return values;
		}
		public ClassCount(final int dim, final double smoothingValue) {
			this.sum = smoothingValue * dim;
			this.values = new double[dim];
			for (int i = 0; i < dim; ++i)
				Arrays.fill(this.values, smoothingValue);
		}
		public void add(final Vector v) {
			v.walk(new Vector.VectorVisitor() {
				@Override
				public void visit(final int index, final double value) {
					values[index] += value;
					sum += value;
				}
			});
		}
	}
	
	private Map<Boolean, ClassCount> classes = null;


	@Override
	public <
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceClassType>,
		InstanceIdType,
		InstanceVectorType extends Vector,
		InstanceClassType extends Boolean
	> void train(List<InstanceType> instances) {
		final int dim = Instances.getDimension(instances);
		final Map<Boolean, ClassCount> classes = new HashMap<Boolean, ClassCount>();
		for (final InstanceType instance : instances) {
			ClassCount count = classes.get(instance.getLabel());
			if (count == null) {
				count = new ClassCount(dim, smoothingValue);
				classes.put(instance.getLabel(), count);
			}
			count.add(instance.getVector());
		}
		this.classes = classes;
	}

	@Override
	public Boolean classify(final Vector x) {
		if (classes == null)
			throw new IllegalStateException("Classifier must be learned first");
		
		Boolean bestClass = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		for (final Map.Entry<Boolean, ClassCount> count : classes.entrySet()) {
			final double[] p = new double[1];
			final double[] values = count.getValue().getValues();
			x.walk(new Vector.VectorVisitor() {
				@Override
				public void visit(final int index, final double value) {
					if (index < values.length)
						p[0] += Math.log(values[index] * value);
				}
			});
			p[0] += Math.log(count.getValue().getSum());
			if (bestScore < p[0]) {
				bestScore = p[0];
				bestClass = count.getKey();
			}
		}
		return bestClass;
	}
	
	public double getSmoothingValue() {
		return smoothingValue;
	}

	public void setSmoothingValue(final double smoothingValue) {
		this.smoothingValue = smoothingValue;
	}
}
