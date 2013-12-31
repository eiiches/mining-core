package net.thisptr.instance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.thisptr.lang.lambda.Lambda1;
import net.thisptr.math.vector.Vector;

public final class Instances {
	private Instances() { }

	public static <
		InstanceIdType,
		InstanceVectorType extends Vector,
		InstanceType extends Instance<InstanceIdType, InstanceVectorType>
	>
	int getDimension(final List<InstanceType> instances) {
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
		InstanceIdType,
		InstanceVectorType extends OperatorArgType,
		InstanceLabelType,
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceLabelType>
	>
	List<LabeledInstance<InstanceIdType, OperatorResultType, InstanceLabelType>> transform(final List<InstanceType> instances, final Lambda1<OperatorResultType, OperatorArgType> operator) {
		final List<LabeledInstance<InstanceIdType, OperatorResultType, InstanceLabelType>> result = new ArrayList<>();
		for (final InstanceType instance : instances)
			result.add(transform(instance, operator));
		return result;
	}

	public static <
		OperatorArgType,
		OperatorResultType,
		InstanceIdType,
		InstanceVectorType extends OperatorArgType,
		InstanceLabelType,
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceLabelType>
	>
	LabeledInstance<InstanceIdType, OperatorResultType, InstanceLabelType> transform(final InstanceType instance, final Lambda1<OperatorResultType, OperatorArgType> operator) {
		return new LabeledInstance<InstanceIdType, OperatorResultType, InstanceLabelType>(instance.getId(), operator.invoke(instance.getVector()), instance.getLabel());
	}

	public static <
		OperatorArgType,
		OperatorResultType,
		InstanceIdType,
		InstanceVectorType extends OperatorArgType,
		InstanceType extends Instance<InstanceIdType, InstanceVectorType>
	>
	Instance<InstanceIdType, OperatorResultType> transform(final InstanceType instance, final Lambda1<OperatorResultType, OperatorArgType> operator) {
		return new Instance<InstanceIdType, OperatorResultType>(instance.getId(), operator.invoke(instance.getVector()));
	}

	public static <
		InstanceVectorType,
		InstanceType extends Instance<?, InstanceVectorType>
	>
	List<InstanceVectorType> toVectors(final List<InstanceType> instances) {
		final List<InstanceVectorType> result = new ArrayList<>();
		for (final InstanceType instance : instances)
			result.add(instance.getVector());
		return result;
	}

	public static <
		InstanceIdType,
		InstanceVectorType extends Vector,
		InstanceLabelType,
		InstanceType extends LabeledInstance<InstanceIdType, InstanceVectorType, InstanceLabelType>
	>
	void writeTable(final List<InstanceType> instances, final File file) throws IOException {
		final int dim = Instances.getDimension(instances);
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			for (final InstanceType instance : instances) {
				final StringBuilder builder = new StringBuilder();
				for (int i = 0; i < dim; ++i) {
					builder.append("\t");
					if (i < instance.getVector().size()) {
						builder.append(instance.getVector().get(i));
					} else {
						builder.append("0");
					}
				}
				writer.write(String.format("%s\t%s%s%n", instance.getId(), instance.getLabel(), builder.toString()));
			}
		}
	}
}