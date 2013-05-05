package net.thisptr.string.misc;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class MaximalSubstringTest {
	@Test
	public void test() {
		SuffixTree suffixTree = SuffixTree.build("abracadabra");
		MaximalSubstring ms = new MaximalSubstring(suffixTree);
		List<String> mss = ms.extractAll();
		Collections.sort(mss);
		assertEquals(Arrays.asList("a", "abra"), mss);
	}
}