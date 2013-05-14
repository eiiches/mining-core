package net.thisptr.graph;


public interface IncidenceGraph extends Graph {
	/**
	 * @param src
	 * @return a list of nodes connected by the edges.
	 */
	int[] getOutEdges(int src);
	
	/**
	 * Visits each nodes connected to the source node.
	 * @param src
	 * @param visitor
	 */
	void walkOutEdges(int src, Visitor visitor);
	
	/**
	 * @param src
	 * @return a out-degree.
	 */
	int getOutDegree(int src);
}