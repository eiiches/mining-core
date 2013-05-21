package net.thisptr.graph;

public interface Graph {
	public interface NodeVisitor {
		void visit(int id);
	}
	
	void walkNodes(NodeVisitor visitor);
	
	int[] getNodes();
	
	int nodeCount();
	
	// FIXME
	// Iterable<Integer> nodeSet();
	
	public interface EdgeVisitor {
		void visit(int src, int dest);
	}
	
	void walkEdges(EdgeVisitor visitor);
	
	public interface Edge {
		public int src();
		public int dest();
	}
	
	int edgeCount();
	
	// FIXME
	// Iterable<Edge> edgeSet();

//	public Graph subgraph(final int[] nodes);
}