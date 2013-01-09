package jp.thisptr.structure.graph.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jTest {
	private static enum RelTypes implements RelationshipType {
		LINKS
	}

	public static void main(String[] args) throws InterruptedException {
		final int vertices = 711486;
		final int edges = 18545822;
		
		final GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("/tmp/jawiki");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
		
		final List<Node> nodes = new ArrayList<Node>();
		Transaction tx = graphDb.beginTx();
		try {
			for (int i = 0; i < vertices; ++i) {
				final Node node = graphDb.createNode();
				node.setProperty("id", 0);
				nodes.add(node);
			}
			tx.success();
			System.err.println("All vertices created.");
			for (int i = 0; i < edges; ++i) {
				final int src = RandomUtils.nextInt(vertices);
				final int dst = RandomUtils.nextInt(vertices);
				nodes.get(src).createRelationshipTo(nodes.get(dst), RelTypes.LINKS);
				if (i % 10000 == 0) {
					System.err.printf("%d edges created.%n", i);
					tx.success();
					tx.finish();
					tx = graphDb.beginTx();
				}
			}
			System.err.println("All edges created.");
			tx.success();
		} finally {
			tx.finish();
		}

		System.err.println("Complete");
		System.gc();
		Thread.sleep(Long.MAX_VALUE);
		
		System.err.println(graphDb.toString());
	}

}
