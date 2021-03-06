package net.thisptr.clustering;

import java.util.List;

import net.thisptr.instance.Instance;
import net.thisptr.math.vector.Vector;

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
		
		private List<Instance<Long, Vector>> instances;
	}
	
	public List<List<Instance<Long, Vector>>> run(final List<Instance<Long, Vector>> instances, final int nCluster) {
		return null;
	}
}
