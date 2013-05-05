package net.thisptr.string.suffixarray;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class SaisSuffixArrayBuilderTest {
	@RunWith(Theories.class)
	public static class PredefinedTest {
		private SaisSuffixArrayBuilder sut;
		
		@Before
		public void setUp() {
			sut = new SaisSuffixArrayBuilder();
		}
		
		@DataPoints
		public static String[] texts = new String[] {
			"",
			"a",
			"sasamisan",
			"abracadabra",
			"mmiissiissiippii",
			"mississippi",
			"unt eunt p",
		};
	
		@Theory
		public void test(final String text) {
			final SuffixArray actual = sut.build(text.toCharArray());
			final SuffixArray expected = new NaiveSuffixArrayBuilder().build(text.toCharArray());
			assertEquals(expected, actual);
		}
	}
	
	@RunWith(Theories.class)
	public static class RandomTest {
		private SaisSuffixArrayBuilder sut;
		
		@Before
		public void setUp() {
			sut = new SaisSuffixArrayBuilder();
		}
		
		private static String genTestCase(final int alphabetSize, final int length) {
			final Random random = new Random();
			final StringBuilder builder = new StringBuilder();
			for (int i = 0; i < length; ++i)
				builder.append((char) ('a' + random.nextInt(alphabetSize)));
			return builder.toString();
		}
		
		@DataPoints
		public static String[] genTestCases() {
			final List<String> result = new ArrayList<String>();
			for (int loop = 0; loop < 10; ++loop) {
				for (int alphabetSize = 1; alphabetSize < 10; alphabetSize += 2) {
					result.add(genTestCase(alphabetSize, 2));
					result.add(genTestCase(alphabetSize, 3));
					result.add(genTestCase(alphabetSize, 5));
					result.add(genTestCase(alphabetSize, 8));
					result.add(genTestCase(alphabetSize, 13));
					result.add(genTestCase(alphabetSize, 21));
					result.add(genTestCase(alphabetSize, 34));
					result.add(genTestCase(alphabetSize, 55));
					result.add(genTestCase(alphabetSize, 89));
				}
			}
			return result.toArray(new String[result.size()]);
		}
		
		@Theory
		public void test(final String text) {
			final SuffixArray actual = sut.build(text.toCharArray());
			final SuffixArray expected = new NaiveSuffixArrayBuilder().build(text.toCharArray());
			assertEquals(expected, actual);
		}
	}
}

