package net.thisptr.structure.graph.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.thisptr.structure.graph.Vertex;

public class MutableVertex implements Vertex {
	private List<Vertex> outVertices = new ArrayList<Vertex>();
	private int value = 0;

	@Override
	public List<Vertex> getOutVertices() {
		return outVertices;
	}
	
	public void addOutVertex(final Vertex vertex) {
		outVertices.add(vertex);
	}
}
