package net.thisptr.structure.suffixarray.algorithm;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.thisptr.structure.suffixarray.SuffixTree;

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