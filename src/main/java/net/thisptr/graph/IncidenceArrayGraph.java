package net.thisptr.graph;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;

import java.io.Serializable;
import java.util.NoSuchElementException;

import net.thisptr.lang.NotImplementedException;

public class IncidenceArrayGraph implements IncidenceGraph, Serializable {
	private static final long serialVersionUID = -894557306655225982L;
	
	private Int2ObjectMap<int[]> outEdges;
	
	public IncidenceArrayGraph(final IncidenceGraph orig) {
		outEdges = new Int2ObjectOpenHashMap<int[]>();
		orig.walkNodes(new NodeVisitor() {
			@Override
			public void visit(final int src) {
				outEdges.put(src, orig.getOutEdges(src));
			}
		});
	}

	@Override
	public int[] getOutEdges(int src) {
		final int[] dests = outEdges.get(src);
		if (dests == null)
			throw new NoSuchElementException();
		
		return dests;
	}

	@Override
	public void walkOutEdges(int src, NodeVisitor visitor) {
		final int[] dests = outEdges.get(src);
		if (dests == null)
			throw new NoSuchElementException();
		
		for (final int dest : dests)
			visitor.visit(dest);
	}

	@Override
	public int getOutDegree(int src) {
		final int[] dests = outEdges.get(src);
		if (dests == null)
			throw new NoSuchElementException();
		
		return dests.length;
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
	public void walkEdges(final EdgeVisitor visitor) {
		for (final Int2ObjectMap.Entry<int[]> entry : outEdges.int2ObjectEntrySet())
			for (final int dest : entry.getValue())
				visitor.visit(entry.getIntKey(), dest);
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
