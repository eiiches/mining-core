package jp.thisptr.structure.instance;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

import java.util.List;

import jp.thisptr.math.vector.SparseMapVector;

public final class Transforms {
	private Transforms() { }
	
	public static <InstanceType extends Instance<SparseMapVector>> void idf(final List<InstanceType> instances) {
		final int n = instances.size();
		final int dim = Instances.getDimension(instances);

		// count df(q) first
		final double[] df = new double[dim];
		for (final InstanceType instance : instances)
			for (final Int2DoubleMap.Entry entry : instance.getVector().rawMap().int2DoubleEntrySet())
				if (entry.getDoubleValue() != 0)
					df[entry.getIntKey()] += 1;

		// update values
		for (final InstanceType instance : instances)
			for (final Int2DoubleMap.Entry entry : instance.getVector().rawMap().int2DoubleEntrySet())
				if (entry.getDoubleValue() != 0) {
					final double idf = Math.log(n / df[entry.getIntKey()]);
					entry.setValue(idf);
				}
	}
}