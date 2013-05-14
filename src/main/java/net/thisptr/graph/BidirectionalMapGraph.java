package net.thisptr.graph;

import net.thisptr.lang.NotImplementedException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class BidirectionalMapGraph implements BidirectionalGraph, MutableGraph {
	private Int2ObjectMap<IntSet> outEdges;
	private Int2ObjectMap<IntSet> inEdges;
	
	public BidirectionalMapGraph() {
		this.outEdges = new Int2ObjectOpenHashMap<IntSet>();
		this.inEdges = new Int2ObjectOpenHashMap<IntSet>();
	}
	
	@Override
	public boolean addNode(final int id) {
		if (outEdges.containsKey(id))
			return false;
		
		outEdges.put(id, new IntOpenHashSet());
		inEdges.put(id, new IntOpenHashSet());
		
		return true;
	}

	@Override
	public boolean addEdge(final int src, final int dest) {
		final IntSet dests = outEdges.get(src);
		if (dests == null)
			return false;
		
		if (!outEdges.containsKey(dest))
			return false;
		
		inEdges.get(dest).add(src);
		return dests.add(dest);
	}

	@Override
	public boolean removeNode(final int id) {
		throw new NotImplementedException();
	}

	@Override
	public boolean removeEdge(final int src, final int dest) {
		final IntSet dests = outEdges.get(src);
		if (dests == null)
			return false;
		
		inEdges.get(dest).remove(src);
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
	public int[] getInEdges(int dest) {
		final IntSet srcs = inEdges.get(dest);
		if (srcs == null)
			return null;
		
		return srcs.toArray(new int[srcs.size()]);
	}

	@Override
	public void walkInEdges(int dest, Visitor visitor) {
		final IntSet srcs = inEdges.get(dest);
		if (srcs == null)
			return;
		
		final IntIterator iter = srcs.iterator();
		while (iter.hasNext())
			visitor.visit(iter.nextInt());
	}

	@Override
	public int getInDegree(int dest) {
		final IntSet srcs = inEdges.get(dest);
		if (srcs == null)
			return -1;
		
		return srcs.size();
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