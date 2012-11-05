package jp.thisptr.classifier;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfusionMatrixTest {
	@Test
	public void test() {
		ConfusionMatrix<Integer> m = new ConfusionMatrix<Integer>();
		assertEquals(0, m.getCount(1, 0));
		assertEquals(0, m.getCount(1, 1));
		m.add(1, 0);
		m.add(1, 1);
		assertEquals(0, m.getCount(0, 0));
		assertEquals(1, m.getCount(1, 0));
		assertEquals(1, m.getCount(1, 1));
		assertEquals("[[0,0],[1,1]]", m.toString());
		System.out.println(m.toPrettyString());
	}
}
