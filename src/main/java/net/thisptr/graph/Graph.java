package net.thisptr.graph;

public interface Graph {
	public interface Visitor {
		void visit(int id);
	}
	
	void walkNodes(Visitor visitor);
	int[] getNodes();
}