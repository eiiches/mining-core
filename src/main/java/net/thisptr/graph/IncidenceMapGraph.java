package net.thisptr.graph;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class IncidenceMapGraph implements IncidenceGraph, MutableGraph {
	private Int2ObjectMap<IntSet> outEdges;
	
	public IncidenceMapGraph() {
		this.outEdges = new Int2ObjectOpenHashMap<IntSet>();
	}
	
	@Override
	public boolean addNode(final int id) {
		if (outEdges.containsKey(id))
			return false;
		
		outEdges.put(id, new IntOpenHashSet());
		
		return true;
	}

	@Override
	public boolean addEdge(final int src, final int dest) {
		final IntSet dests = outEdges.get(src);
		if (dests == null)
			return false;
		
		if (outEdges.containsKey(dest))
			dests.add(dest);
		
		return true;
	}

	@Override
	public boolean removeNode(final int id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeEdge(final int src, final int dest) {
		final IntSet dests = outEdges.get(src);
		if (dests == null)
			return false;
		
		return dests.remove(dest);
	}

	@Override
	public int[] getOutEdges(final int src) {
		final IntSet dests = outEdges.get(src);
		if (dests == null)
			return null;
		
		return dests.toArray(new int[dests.size()]);
	}

	@Override
	public int getOutDegree(final int src) {
		final IntSet dests = outEdges.get(src);
		if (dests == null)
			return -1;
		
		return dests.size();
	}

	@Override
	public void walkOutEdges(final int src, final Visitor visitor) {
		final IntSet dests = outEdges.get(src);
		if (dests == null)
			return;
		
		final IntIterator iter = dests.iterator();
		while (iter.hasNext())
			visitor.visit(iter.nextInt());
	}

	@Override
	public void walkNodes(final Visitor visitor) {
		final IntIterator iter = outEdges.keySet().iterator();
		while (iter.hasNext())
			visitor.visit(iter.nextInt());
	}

	@Override
	public int[] getNodes() {
		return outEdges.keySet().toArray(new int[outEdges.size()]);
	}
}