package net.thisptr.graph;

public interface MutableGraph extends Graph {
	/**
	 * @param id
	 * @return true if the node is newly added.
	 */
	boolean addNode(int id);
	
	/**
	 * @param src
	 * @param dest
	 * @return true if the edge is newly added.
	 */
	boolean addEdge(int src, int dest);
	
	/**
	 * @param id
	 * @return true if the node is successfully removed.
	 */
	boolean removeNode(int id);
	
	/**
	 * @param src
	 * @param dest
	 * @return true if the edge is successfully removed.
	 */
	boolean removeEdge(int src, int dest);
}