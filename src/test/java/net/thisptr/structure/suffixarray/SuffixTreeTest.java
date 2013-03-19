package net.thisptr.structure.suffixarray;

import static org.junit.Assert.assertArrayEquals;
import net.thisptr.structure.suffixarray.SuffixTree;

import org.junit.Test;

public class SuffixTreeTest {
	
	@Test
	public void test() {
		SuffixTree suffixTree = SuffixTree.build("abracadabra$");
		assertArrayEquals(new int[] {
				    // [i]  BW suffix
				11, // [0]  a  $
				10, // [1]  r  a$ *
				 7, // [2]  d  abra$ *
				 0, // [3]  $  abracadabra$ *
				 3, // [4]  r  acadabra$
				 5, // [5]  c  adabra$
				 8, // [6]  a  bra$
				 1, // [7]  a  bracadabra$
				 4, // [8]  a  cadabra$
				 6, // [9]  a  dabra$
				 9, // [10] b  ra$
				 2, // [11] b  racadabra$
		}, suffixTree.getSuffixArray());
		assertArrayEquals(new int[] { 2,  1,  6, 10,  0 }, suffixTree.getBeginIndices());
		assertArrayEquals(new int[] { 4,  6,  8, 12, 12 }, suffixTree.getEndIndices());
		assertArrayEquals(new int[] { 4,  1,  3,  2,  0 }, suffixTree.getDepthValues());
	}
	
	@Test
	public void test2() {
		SuffixTree suffixTree = SuffixTree.build("abracadabra");
		assertArrayEquals(new int[] {
				    // [i]  suffix
				10, // [0]  a *
				 7, // [1]  abra *
				 0, // [2]  abracadabra *
				 3, // [3]  acadabra
				 5, // [4]  adabra
				 8, // [5]  bra
				 1, // [6]  bracadabra
				 4, // [7]  cadabra
				 6, // [8]  dabra
				 9, // [9]  ra
				 2, // [10] racadabra
		}, suffixTree.getSuffixArray());
		assertArrayEquals(new int[] { 1,  0,  5,  9,  0 }, suffixTree.getBeginIndices());
		assertArrayEquals(new int[] { 3,  5,  7, 11, 11 }, suffixTree.getEndIndices());
		assertArrayEquals(new int[] { 4,  1,  3,  2,  0 }, suffixTree.getDepthValues());
	}
}
