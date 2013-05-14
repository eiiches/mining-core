package net.thisptr.graph;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;

public class IncidenceArrayGraph implements IncidenceGraph {
	private Int2ObjectMap<int[]> outEdges;
	
	public IncidenceArrayGraph(final IncidenceGraph orig) {
		outEdges = new Int2ObjectOpenHashMap<int[]>();
		orig.walkNodes(new Visitor() {
			@Override
			public void visit(final int src) {
				outEdges.put(src, orig.getOutEdges(src));
			}
		});
	}

	@Override
	public int[] getOutEdges(int src) {
		return outEdges.get(src);
	}

	@Override
	public void walkOutEdges(int src, Visitor visitor) {
		final int[] dests = outEdges.get(src);
		if (dests == null)
			return;
		
		for (final int dest : dests)
			visitor.visit(dest);
	}

	@Override
	public int getOutDegree(int src) {
		final int[] dests = outEdges.get(src);
		if (dests == null)
			return -1;
		
		return dests.length;
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
