package jp.thisptr.core.collection;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import jp.thisptr.core.collection.DefaultMap;
import jp.thisptr.core.lambda.util.Lambdas;
import jp.thisptr.core.util.CollectionUtils;

import org.junit.Test;

public class CollectionUtilsTest {

	@Test
	public void testDefualtMap() {
		DefaultMap<String, Integer> a = CollectionUtils.defualtMap(new HashMap<String, Integer>(), Lambdas.constant(0));
		assertEquals(Integer.valueOf(0), a.get("a"));
	}

}