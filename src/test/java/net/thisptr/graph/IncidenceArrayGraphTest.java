package net.thisptr.graph;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IncidenceArrayGraphTest {

	@Test
	public void test() {
		final IncidenceMapGraph tmp = new IncidenceMapGraph();
		tmp.addNode(10);
		tmp.addNode(11);
		tmp.addEdge(10, 11);
		
		final IncidenceArrayGraph sut = new IncidenceArrayGraph(tmp);
		
		assertEquals(1, sut.getOutDegree(10));
		assertArrayEquals(new int[] { 11 }, sut.getOutEdges(10));
		
		assertEquals(0, sut.getOutDegree(11));
		assertArrayEquals(new int[] { }, sut.getOutEdges(11));
	}
}
