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

	public static <OperatorArgType, OperatorResultType, InputVectorType extends OperatorArgType, LabelType> List<LabeledInstance<OperatorResultType, LabelType>> transform(final List<LabeledInstance<InputVectorType, LabelType>> instances, final Lambda1<OperatorResultType, OperatorArgType> operator) {
		final List<LabeledInstance<OperatorResultType, LabelType>> result = new ArrayList<>();
		for (final LabeledInstance<InputVectorType, LabelType> instance : instances)
			result.add(transform(instance, operator));
		return result;
	}

	public static <OperatorArgType, OperatorResultType, InputVectorType extends OperatorArgType, LabelType> LabeledInstance<OperatorResultType, LabelType> transform(final LabeledInstance<InputVectorType, LabelType> instance, final Lambda1<OperatorResultType, OperatorArgType> operator) {
		return new LabeledInstance<OperatorResultType, LabelType>(instance.getId(), operator.invoke(instance.getVector()), instance.getLabel());
	}
}