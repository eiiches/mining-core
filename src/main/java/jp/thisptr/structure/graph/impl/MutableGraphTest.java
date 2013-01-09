package jp.thisptr.structure.graph.impl;

import jp.thisptr.structure.graph.Vertex;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

public class MutableGraphTest {

	public static void main(String[] args) throws InterruptedException {
		final int vertices = 711486;
		final int edges = 18545822;
		
		final MutableGraph graph = new MutableGraph();
		
		// generate vertices
		for (int i = 0; i < vertices; ++i)
			graph.addVertices(new MutableVertex());
		
		// randomly connect vertices
		for (int i = 0; i < edges; ++i) {
			final int src = RandomUtils.nextInt(vertices);
			final int dst = RandomUtils.nextInt(vertices);
			final MutableVertex srcVertex = (MutableVertex) graph.getVertices().get(src);
			final Vertex dstVertex = graph.getVertices().get(dst);
			srcVertex.addOutVertex(dstVertex);
		}
		
		System.err.println("Complete");
		System.gc();
		Thread.sleep(Long.MAX_VALUE);
		
		System.err.println(graph.toString());
	}

}
