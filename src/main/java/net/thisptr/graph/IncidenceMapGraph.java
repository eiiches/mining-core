package net.thisptr.graph;

import net.thisptr.lang.NotImplementedException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class IncidenceMapGraph implements IncidenceMutableGraph {
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
	public void walkOutEdges(final int src, final NodeVisitor visitor) {
		final IntSet dests = outEdges.get(src);
		if (dests == null)
			return;
		
		final IntIterator iter = dests.iterator();
		while (iter.hasNext())
			visitor.visit(iter.nextInt());
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
	
//	@Override
//	public Graph subgraph(final int[] nodes) {
////		outEdges.key
//		// TODO Auto-generated method stub
//		return null;
//	}
}