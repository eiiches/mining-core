package net.thisptr.structure.graph.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.thisptr.structure.graph.Graph;
import net.thisptr.structure.graph.Vertex;

public class MutableGraph implements Graph {
	private List<Vertex> vertices = new ArrayList<Vertex>();
	
	@Override
	public List<Vertex> getVertices() {
		return vertices;
	}
	
	public void addVertices(final Vertex vertex) {
		vertices.add(vertex);
	}
}