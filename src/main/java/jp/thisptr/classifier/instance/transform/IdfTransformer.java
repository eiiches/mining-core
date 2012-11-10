package jp.thisptr.classifier.instance.transform;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import jp.thisptr.classifier.instance.Instance;
import jp.thisptr.classifier.instance.Instances;
import jp.thisptr.math.vector.d.SparseMapVector;

public class IdfTransformer extends Transformer<SparseMapVector> {
	@Override
	public void transform(final Instances<SparseMapVector, ?> instances) {
		final int n = instances.size();
		final int dim = instances.dim();
		
		// count df(q) first
		final double[] df = new double[dim];
		for (final Instance<SparseMapVector, ?> instance : instances)
			for (final Int2DoubleMap.Entry entry : instance.getVector().rawMap().int2DoubleEntrySet())
				if (entry.getDoubleValue() != 0)
					df[entry.getIntKey()] += 1;
		
		// update values
		for (final Instance<SparseMapVector, ?> instance : instances)
			for (final Int2DoubleMap.Entry entry : instance.getVector().rawMap().int2DoubleEntrySet())
				if (entry.getDoubleValue() != 0) {
					final double idf = Math.log(n / df[entry.getIntKey()]);
					entry.setValue(idf);
				}
	}
}