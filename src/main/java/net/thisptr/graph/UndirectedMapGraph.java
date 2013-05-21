package net.thisptr.graph;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;

public class UndirectedMapGraph extends BidirectionalMapGraph {
	@Override
	public boolean addEdge(int src, int dest) {
		super.addEdge(dest, src);
		return super.addEdge(src, dest);
	}

	@Override
	public void walkEdges(final EdgeVisitor visitor) {
		for (final Int2ObjectMap.Entry<IntSet> entry : outEdges.int2ObjectEntrySet()) {
			final IntIterator iter = entry.getValue().iterator();
			while (iter.hasNext()) {
				final int id = iter.nextInt();
				if (entry.getIntKey() < id)
					visitor.visit(entry.getIntKey(), id);
			}
		}
	}
}
