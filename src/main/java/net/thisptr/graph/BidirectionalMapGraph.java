package net.thisptr.graph;

import java.util.NoSuchElementException;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.thisptr.lang.NotImplementedException;

public class BidirectionalMapGraph implements BidirectionalMutableGraph {
	protected Int2ObjectMap<IntSet> outEdges;
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
			throw new NoSuchElementException();
		
		if (!outEdges.containsKey(dest))
			throw new NoSuchElementException();
		
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
			throw new NoSuchElementException();
		
		inEdges.get(dest).remove(src);
		return dests.remove(dest);
	}

	@Override
	public int[] getOutEdges(final int src) {
		final IntSet dests = outEdges.get(src);
		if (dests == null)
			throw new NoSuchElementException();
		
		return dests.toArray(new int[dests.size()]);
	}

	@Override
	public int getOutDegree(final int src) {
		final IntSet dests = outEdges.get(src);
		if (dests == null)
			throw new NoSuchElementException();
		
		return dests.size();
	}

	@Override
	public void walkOutEdges(final int src, final NodeVisitor visitor) {
		final IntSet dests = outEdges.get(src);
		if (dests == null)
			throw new NoSuchElementException();
		
		final IntIterator iter = dests.iterator();
		while (iter.hasNext())
			visitor.visit(iter.nextInt());
	}

	@Override
	public int[] getInEdges(int dest) {
		final IntSet srcs = inEdges.get(dest);
		if (srcs == null)
			throw new NoSuchElementException();
		
		return srcs.toArray(new int[srcs.size()]);
	}

	@Override
	public void walkInEdges(int dest, NodeVisitor visitor) {
		final IntSet srcs = inEdges.get(dest);
		if (srcs == null)
			throw new NoSuchElementException();
		
		final IntIterator iter = srcs.iterator();
		while (iter.hasNext())
			visitor.visit(iter.nextInt());
	}

	@Override
	public int getInDegree(int dest) {
		final IntSet srcs = inEdges.get(dest);
		if (srcs == null)
			throw new NoSuchElementException();
		
		return srcs.size();
	}

	@Override
	public void walkNodes(final NodeVisitor visitor) {
		final IntIterator iter = outEdges.keySet().iterator();
		while (iter.hasNext())
			visitor.visit(iter.nextInt());
	}

	@Override
	public int[] getNodes() {
		return outEdges.keySet().toArray(new int[outEdges.size()]);
	}

	@Override
	public void walkEdges(EdgeVisitor visitor) {
		for (final Int2ObjectMap.Entry<IntSet> entry : outEdges.int2ObjectEntrySet()) {
			final IntIterator iter = entry.getValue().iterator();
			while (iter.hasNext())
				visitor.visit(entry.getIntKey(), iter.nextInt());
		}
	}

	@Override
	public int nodeCount() {
		return outEdges.size();
	}

	@Override
	public int edgeCount() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
}