package jp.thisptr.recommend;

import java.util.ArrayList;
import java.util.List;

import jp.thisptr.core.util.ScoredItem;
import jp.thisptr.instance.Instance;
import jp.thisptr.instance.Instances;
import jp.thisptr.math.distribution.multivariate.GibbsDistribution;
import jp.thisptr.math.distribution.multivariate.MultivariateDistribution;
import jp.thisptr.math.structure.vector.SparseMapVector;

public class MaxentSets implements SetExpander {
	private final Instances<SparseMapVector, Void> instances;
	
	public MaxentSets(final Instances<SparseMapVector, Void> instances) {
		this.instances = instances;
	}

	@Override
	public List<ScoredItem<Instance<SparseMapVector, Void>, Double>> expand(final List<Instance<SparseMapVector, Void>> seeds) {
		final double z;
		final double[] lambda;
		final GibbsDistribution gibbs = new GibbsDistribution(z, lambda);
		
		final List<ScoredItem<Instance<SparseMapVector, Void>, Double>> result = new ArrayList<>();
		for (final Instance<SparseMapVector, Void> instance : instances) {
			double score = gibbs.at(instance.getVector());
			result.add(new ScoredItem<Instance<SparseMapVector, Void>, Double>(instance, score));
		}
		return result;
	}
}
