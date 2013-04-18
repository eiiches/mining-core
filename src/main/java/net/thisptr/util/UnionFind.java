package net.thisptr.util;

import java.util.Arrays;


public class UnionFind {
	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	
	/**
	 * Each positive elements represents its parent node id.
	 * -1 is used to indicate that the element is not used.
	 */
	private int[] par;
	
	public UnionFind() {
		this(DEFAULT_INITIAL_CAPACITY);
	}
	
	public UnionFind(final int initialCapacity) {
		ensureCapacity(initialCapacity);
	}
	
	/**
	 * Check if the given indices all belong to the same union.
	 * @param ids
	 * @return 
	 */
	public boolean isUnion(final int... ids) {
		int check = -1;
		for (final int id : ids) {
			final int gid = find(id);
			if (check >= 0 && check != gid)
				return false;
			check = gid;
		}
		return true;
	}
	
	/**
	 * Find the smallest id of the group.
	 * @param index
	 * @return the smallest id of the group containing the given id.
	 */
	public int find(final int id) {
		if (id >= par.length)
			return id;
		
		final int parId = par[id];
		
		// not used yet, meaning that the id itself makes a group.
		if (parId < 0)
			return id;
		
		if (parId == id)
			return id;
		
		return par[id] = find(parId);
	}
	
	/**
	 * Unite two groups, each containing id1, id2.
	 * @param id1
	 * @param id2
	 * @return the smallest id of the group united.
	 */
	public int unite(final int id1, final int id2) {
		final int g1 = find(id1);
		final int g2 = find(id2);
		
		if (g1 == g2)
			return g1;
		
		// FIXME: should use rank information to reduce time complexity.
		if (g1 < g2) {
			ensureCapacity(g2 + 1);
			return par[g2] = g1;
		} else {
			ensureCapacity(g1 + 1);
			return par[g1] = g2;
		}
	}
	
	private void ensureCapacity(final int requiredCapacity) {
		if (par == null) {
			par = new int[requiredCapacity];
			Arrays.fill(par, -1);
		} else {
			final int currentSize = par.length;
			
			// decide next size
			int nextSize = currentSize;
			while (nextSize < requiredCapacity)
				nextSize *= 2;
			
			par = Arrays.copyOf(par, nextSize);
			Arrays.fill(par, currentSize, nextSize, -1);
		}
	}
}
