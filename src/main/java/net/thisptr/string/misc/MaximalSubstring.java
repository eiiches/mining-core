package net.thisptr.string.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaximalSubstring {
	private final SuffixTree suffixTree;
	
	public MaximalSubstring(final SuffixTree suffixTree) {
		this.suffixTree = suffixTree;
	}

	public List<String> extractAll() {
		final int n = suffixTree.size();
		final int[] suffixArray = suffixTree.getSuffixArray();
		final char[] text = suffixTree.getText();
	
		final int[] rank = new int[n];
		for (int i = 0, r = 0; i < n; ++i) {
			if (i == 0 || text[(suffixArray[i] + n - 1) % n] != text[(suffixArray[i - 1] + n - 1) % n])
				r++;
			rank[i] = r;
		}
	
		final int[] beginIndices = suffixTree.getBeginIndices();
		final int[] endIndices = suffixTree.getEndIndices();
		final int[] depthValues = suffixTree.getDepthValues();
		
		final List<String> result = new ArrayList<String>();
		for (int i = 0; i < beginIndices.length; ++i)
			if (rank[endIndices[i] - 1] - rank[beginIndices[i]] > 0 && depthValues[i] > 0) {
				final int beginIndex = suffixArray[beginIndices[i]];
				final int endIndex = beginIndex + depthValues[i];
				result.add(new String(Arrays.copyOfRange(text, beginIndex, endIndex)));
			}
		
		return result;
	}
}