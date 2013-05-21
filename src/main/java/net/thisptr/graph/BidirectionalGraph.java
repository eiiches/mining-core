package net.thisptr.graph;

public interface BidirectionalGraph extends IncidenceGraph {
	/**
	 * @param dest
	 * @return a list of nodes connected by the edges.
	 */
	int[] getInEdges(int dest);
	
	/**
	 * Visits each nodes connected to the source node.
	 * @param dest
	 * @param visitor
	 */
	void walkInEdges(int dest, NodeVisitor visitor);
	
	/**
	 * @param dest
	 * @return a in-degree.
	 */
	int getInDegree(int dest);
}