package jp.thisptr.lang.lambda;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import jp.thisptr.util.Lambdas;

import org.junit.Test;

public class LambdaTest {
	
	@Test
	public void testStringToLong() {
		assertEquals(Long.valueOf(123), Lambdas.toLong().invoke("123"));
	}
	
	@Test
	public void testMax() {
		assertEquals(Integer.valueOf(3), Lambdas.<Integer>max().invoke(3, 1));
		assertEquals(Integer.valueOf(3), Lambdas.max(Integer.class).invoke(3, 1));
	}
	
	@Test
	public void testMul() {
		Double actual = Lambdas.mul(Double.class).invoke(2.0, 5.0);
		assertEquals(Double.valueOf(10), actual);
		assertEquals(Integer.valueOf(10), Lambdas.mul(Integer.class).invoke(2, 5));
		assertEquals(Integer.valueOf(10), Lambdas.<Integer>mul().invoke(2, 5));
	}
	
	@Test
	public void testBind() {
		assertEquals(Integer.valueOf(11), Lambdas.<Integer>add().bind(1).invoke(10));
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void testDefaultConstructor() {
		assertEquals(new ArrayList(),  Lambdas.<ArrayList>constructor(ArrayList.class).invoke());
	}
	
	@Test
	public void testDefaultConstructor2() {
		assertEquals(Integer.valueOf(1), Lambdas.<Integer>constructor(Integer.class, "1").invoke());
	}
}
