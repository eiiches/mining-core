package net.thisptr.neuralnet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class is not thread-safe.
 */
public class DoubleArrayPool {
	private Map<Integer, LinkedList<double[]>> pool = new HashMap<>();

	public double[] borrowArray(final int length) {
		final LinkedList<double[]> freeList = pool.get(length);
		if (freeList == null || freeList.isEmpty())
			return new double[length];
		return freeList.pop();
	}
	
	public double[][] borrowArrays(final int length, final int count) {
		final LinkedList<double[]> freeList = pool.get(length);

		final double[][] resources = new double[count][];
		int i = 0;
		if (freeList != null) {
			final int loop = Math.min(freeList.size(), count);
			for (; i < loop; ++i)
				resources[i] = freeList.pop();
		}
		for (; i < count; ++i)
			resources[i] = new double[length];
		return resources;
	}

	public void returnArray(final double[] resource) {
		LinkedList<double[]> freeList = pool.get(resource.length);
		if (freeList == null) {
			freeList = new LinkedList<double[]>();
			pool.put(resource.length, freeList);
		}
		freeList.add(resource);
	}
	
	public void returnArrays(final double[][] resources) {
		for (final double[] resource : resources)
			returnArray(resource);
	}

	public void release() {
		pool = new HashMap<>();
	}
}