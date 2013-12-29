package net.thisptr.neuralnet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class is not thread-safe.
 */
public class BooleanArrayPool {
	private Map<Integer, LinkedList<boolean[]>> pool = new HashMap<>();

	public boolean[] borrowArray(final int length) {
		final LinkedList<boolean[]> freeList = pool.get(length);
		if (freeList == null || freeList.isEmpty())
			return new boolean[length];
		return freeList.pop();
	}

	public boolean[][] borrowArrays(final int length, final int count) {
		final LinkedList<boolean[]> freeList = pool.get(length);

		final boolean[][] resources = new boolean[count][];
		int i = 0;
		if (freeList != null) {
			final int loop = Math.min(freeList.size(), count);
			for (; i < loop; ++i)
				resources[i] = freeList.pop();
		}
		for (; i < count; ++i)
			resources[i] = new boolean[length];
		return resources;
	}

	public void returnArray(final boolean[] resource) {
		LinkedList<boolean[]> freeList = pool.get(resource.length);
		if (freeList == null) {
			freeList = new LinkedList<boolean[]>();
			pool.put(resource.length, freeList);
		}
		freeList.add(resource);
	}
	
	public void returnArrays(final boolean[][] resources) {
		for (final boolean[] resource : resources)
			returnArray(resource);
	}

	public void release() {
		pool = new HashMap<>();
	}
}