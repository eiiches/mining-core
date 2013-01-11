package jp.thisptr.recommend;

import java.util.List;

import jp.thisptr.core.util.ScoredItem;
import jp.thisptr.instance.Instance;
import jp.thisptr.math.structure.vector.SparseMapVector;

public interface SetExpander {
	List<ScoredItem<Instance<SparseMapVector, Void>, Double>> expand(final List<Instance<SparseMapVector, Void>> seeds);
}
