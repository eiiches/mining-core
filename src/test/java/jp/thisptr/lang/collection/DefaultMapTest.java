package jp.thisptr.lang.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

public class DefaultMapTest {
	private DefaultMap<String, Integer> sut = null;

	@Before
	public void setUp() {
		sut = new DefaultMap<String, Integer>(new HashMap<String, Integer>()) {
			@Override
			public Integer defaultValue() {
				return 0;
			}
		};
	}

	@Test
	public void testFindWhenEmpty() {
		assertNull(sut.find("some_non_existent_key"));
	}
	
	@Test
	public void testGetWhenEmpty() {
		assertEquals(0, sut.get("some_non_existent_key").intValue());
	}
	
	@Test
	public void testGetWhenNotEmpty() {
		sut.put("key", 10);
		assertEquals(10, sut.get("key").intValue());
	}
	
	@Test
	public void testFindWhenNotEmpty() {
		sut.put("key", 10);
		assertEquals(10, sut.find("key").intValue());
	}
}