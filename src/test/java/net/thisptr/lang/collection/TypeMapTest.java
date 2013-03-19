package net.thisptr.lang.collection;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TypeMapTest {
	private TypeMap<Number> sut = null;

	@Before
	public void setUp() {
		sut = new TypeMap<Number>();
	}

	@Test
	public void test() {
		sut.put(Integer.class, 10);
		sut.put(Double.class, 10.5);
		
		assertEquals(Integer.valueOf(10), sut.get(Integer.class));
		assertEquals(Double.valueOf(10.5), sut.get(Double.class));
	}

}
