package net.thisptr.structure.instance;

import java.util.ArrayList;
import java.util.List;

import net.thisptr.lang.lambda.Lambda1;
import net.thisptr.math.vector.Vector;

public final class Instances {
	private Instances() { }

	public static <InstanceType extends Instance<? extends Vector>> int getDimension(final List<InstanceType> instances) {
		int result = 0;
		for (final InstanceType instance : instances) {
			final int size = instance.getVector().size();
			if (size > result)
				result = size;
		}
		return result;
	}

	public static <
		OperatorArgType,
		OperatorResultType,
		InstanceVectorType extends OperatorArgType,
		InstanceLabelType,
		InstanceType extends LabeledInstance<InstanceVectorType, InstanceLabelType>
	>
	List<LabeledInstance<OperatorResultType, InstanceLabelType>> transform(final List<InstanceType> instances, final Lambda1<OperatorResultType, OperatorArgType> operator) {
		final List<LabeledInstance<OperatorResultType, InstanceLabelType>> result = new ArrayList<>();
		for (final LabeledInstance<InstanceVectorType, InstanceLabelType> instance : instances)
			result.add(transform(instance, operator));
		return result;
	}

	public static <
		OperatorArgType,
		OperatorResultType,
		InstanceVectorType extends OperatorArgType,
		InstanceLabelType,
		InstanceType extends LabeledInstance<InstanceVectorType, InstanceLabelType>
	>
	LabeledInstance<OperatorResultType, InstanceLabelType> transform(final InstanceType instance, final Lambda1<OperatorResultType, OperatorArgType> operator) {
		return new LabeledInstance<OperatorResultType, InstanceLabelType>(instance.getId(), operator.invoke(instance.getVector()), instance.getLabel());
	}

	public static <
		InstanceVectorType,
		InstanceType extends Instance<InstanceVectorType>
	>
	List<InstanceVectorType> toVectors(final List<InstanceType> instances) {
		final List<InstanceVectorType> result = new ArrayList<>();
		for (final InstanceType instance : instances)
			result.add(instance.getVector());
		return result;
	}
}