package net.thisptr.string.suffixarray;

import static org.junit.Assert.assertArrayEquals;

import net.thisptr.string.suffixarray.NaiveSuffixArrayBuilder;
import net.thisptr.string.suffixarray.SuffixArray;

import org.junit.Before;
import org.junit.Test;

public class NaiveSuffixArrayBuilderTest {
	private NaiveSuffixArrayBuilder sut;

	@Before
	public void setUp() {
		sut = new NaiveSuffixArrayBuilder();
	}

	@Test
	public void test() {
		final SuffixArray actual = sut.build("abracadabra".toCharArray());
		final int[] expected = new int[] {
				10, // a
				7,  // abra
				0,  // abracadabra
				3,  // acadabra
				5,  // adabra
				8,  // bra
				1,  // bracadabra
				4,  // cadabra
				6,  // dabra
				9,  // ra
				2,  // racadabra
		};
		assertArrayEquals(expected, actual.intArray());
	}
}
