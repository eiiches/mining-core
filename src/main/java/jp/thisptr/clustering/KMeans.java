package jp.thisptr.clustering;

import java.util.List;

import jp.thisptr.math.vector.SparseMapVector;
import jp.thisptr.structure.instance.Instance;

public class KMeans {
	public static class Cluster {
		/**
		 * Sum of the squared errors between the centroid and the instances.
		 */
		private double error;
		
		/**
		 * Centroid of this cluster.
		 */
		private double[] centroid;
		
		private List<Instance<SparseMapVector>> instances;
	}
	
	public List<List<Instance<SparseMapVector>>> run(final List<Instance<SparseMapVector>> instances, final int nCluster) {
		return null;
	}
}
