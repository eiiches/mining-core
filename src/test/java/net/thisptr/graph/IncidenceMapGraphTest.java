package net.thisptr.graph;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class IncidenceMapGraphTest {
	
	private IncidenceMapGraph sut;

	@Before
	public void setUp() {
		sut = new IncidenceMapGraph();
	}

	@Test
	public void test() {
		sut.addNode(10);
		sut.addNode(11);
		sut.addEdge(10, 11);
		
		assertEquals(1, sut.getOutDegree(10));
		assertArrayEquals(new int[] { 11 }, sut.getOutEdges(10));
		
		assertEquals(0, sut.getOutDegree(11));
		assertArrayEquals(new int[] { }, sut.getOutEdges(11));
	}
}
