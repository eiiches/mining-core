package jp.thisptr.core.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import jp.thisptr.lang.ValueError;
import jp.thisptr.lang.generator.Generators;
import jp.thisptr.lang.tuple.Pair;
import jp.thisptr.util.Lambdas;
import jp.thisptr.util.OutputParameter;
import jp.thisptr.util.Predicates;

import org.junit.Test;

public class GeneratorTest {

	@Test
	public void testToList() {
		List<Integer> actual = Generators.array(Arrays.asList(1, 2, 3)).toList();
		assertEquals(Arrays.asList(1, 2, 3), actual);
	}

	@Test
	public void testMap() {
		List<Integer> actual = Generators.array(1, 2, 3, 4, 5).map(Lambdas.increment()).toList();
		assertEquals(Arrays.asList(2, 3, 4, 5, 6), actual);
	}
	
	@Test
	public void testFilter() {
		List<Integer> actual = Generators.array(Arrays.asList(1, 2, 3, 4, 5)).filter(Predicates.isOdd()).toList();
		assertEquals(Arrays.asList(1, 3, 5), actual);
	}

	@Test
	public void testHead() {
		List<Integer> actual = Generators.array(Arrays.asList(1, 2, 3, 4, 5)).head(3).toList();
		assertEquals(Arrays.asList(1, 2, 3), actual);
	}

	@Test
	public void testTail() {
		List<Integer> actual = Generators.array(Arrays.asList(1, 2, 3, 4, 5)).tail(3).toList();
		assertEquals(Arrays.asList(3, 4, 5), actual);
	}
	
	@Test
	public void testFoldl() {
		assertEquals(Integer.valueOf(15), Generators.array(1, 2, 3, 4, 5).foldl(Lambdas.<Integer>add()).eval());
		assertEquals(Double.valueOf(15.0), Generators.array(1.0, 2.0, 3.0, 4.0, 5.0).foldl(Lambdas.<Double>add()).eval(), 0.00001);
		assertEquals(Integer.valueOf(5), Generators.array(1, 2, 3, 4, 5).foldl(Lambdas.<Integer>max()).eval());
		assertEquals(Integer.valueOf(16), Generators.array(1, 2, 3, 4, 5).foldl(Lambdas.<Integer>add(), 1).eval());
		assertEquals(Integer.valueOf(3), Generators.array(1, 2).foldl(Lambdas.<Integer>add()).eval());
		assertEquals(Integer.valueOf(1), Generators.array(1).foldl(Lambdas.<Integer>add()).eval());
		assertEquals(Integer.valueOf(1), Generators.<Integer>array().foldl(Lambdas.<Integer>add(), 1).eval());
		try {
			Generators.<Integer>array().foldl(Lambdas.<Integer>add()).eval();
			fail("ValueError is expected to be thrown.");
		} catch (ValueError e) { }
	}
	
	@Test
	public void testBuffer() {
		List<Integer> array = Generators.sequence(3).toList();
		OutputParameter<Future<?>> tokens = new OutputParameter<Future<?>>();
		assertEquals(array, Generators.sequence(3).buffer(tokens).toList());
		for (Future<?> token : tokens.get())
			token.cancel(true);
	}
	
	@Test
	public void testSequence() {
		/* no arguments */
		assertEquals(Arrays.asList(0, 1, 2), Generators.sequence().head(3).toList());
		/* length */
		assertEquals(Arrays.asList(0, -1, -2), Generators.sequence(-3).toList());
		assertEquals(Arrays.asList(0), Generators.sequence(-1).toList());
		assertEquals(Arrays.asList(), Generators.sequence(0).toList());
		assertEquals(Arrays.asList(0), Generators.sequence(1).toList());
		assertEquals(Arrays.asList(0, 1, 2), Generators.sequence(3).toList());
		/* range */
		assertEquals(Arrays.asList(0), Generators.sequence(0, 1).toList());
		assertEquals(Arrays.asList(0, 1, 2), Generators.sequence(0, 3).toList());
		assertEquals(Arrays.asList(), Generators.sequence(1, 1).toList());
		assertEquals(Arrays.asList(-1, 0, 1), Generators.sequence(-1, 2).toList());
		assertEquals(Arrays.asList(1, 0, -1), Generators.sequence(1, -2).toList());
		/* range with step */
		assertEquals(Arrays.asList(0, 2, 4), Generators.sequence(0, 5, 2).toList());
		assertEquals(Arrays.asList(0, 2, 4), Generators.sequence(0, 6, 2).toList());
		assertEquals(Arrays.asList(), Generators.sequence(0, -3, 2).toList());
		try {
			Generators.sequence(3, 5, 0);
			fail("ValueError must be thrown.");
		} catch (ValueError e) { }
	}
	
	@Test
	public void testGroup() {
		assertEquals(Arrays.asList(
				Arrays.asList(0, 0),
				Arrays.asList(1, 1),
				Arrays.asList(2)
		), Generators.array(0, 0, 1, 1, 2).group().toList());
	}
	
	@Test
	public void testGroupEqual() {
		assertEquals(Arrays.asList(
				Arrays.asList(0, 0),
				Arrays.asList(1, 1),
				Arrays.asList(2)
		), Generators.array(0, 0, 1, 1, 2).group(Lambdas.<Integer>equals()).toList());
	}
	
	@Test
	public void testChain() {
		assertEquals(Arrays.asList(1, 2, 3, 4), Generators.array(1, 2).chain(Generators.array(3, 4)).toList());
	}
	
	@Test
	public void testWindow() {
		assertEquals(Arrays.asList(
				Arrays.asList(0, 1),
				Arrays.asList(1, 2),
				Arrays.asList(2, 3)
		), Generators.array(0, 1, 2, 3).window(2).toList());
		
		assertEquals(Arrays.<List<Integer>>asList(), Generators.array(0, 1, 2).window(4).toList());
		assertEquals(Arrays.<List<Integer>>asList(Arrays.asList(0)), Generators.array(0).window(1).toList());
		assertEquals(Arrays.<List<Integer>>asList(Arrays.asList(0, 1)), Generators.array(0, 1).window(2).toList());
	}
	
	@Test
	public void testEval() {
		assertEquals(Integer.valueOf(0), Generators.single(0).eval());
		try {
			Generators.empty().eval();
			fail("ValueError must be thrown.");
		} catch (ValueError e) { }
	}
	
	@Test
	public void testEach() {
		assertEquals(Integer.valueOf(10), Generators.single(0).each(Lambdas.<Integer>add().bind(10)));
		assertNull(Generators.<Integer>empty().each(Lambdas.<Integer>add().bind(10)));
	}
	
	@Test
	public void testUnique() {
		assertEquals(Arrays.asList(0, 1), Generators.array(0, 0, 1, 1).unique().toList());
	}
}