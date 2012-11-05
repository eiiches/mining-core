package jp.thisptr.structure.suffixarray;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import jp.thisptr.core.tuple.Pair;

public final class SuffixTree {
	private final SuffixArray suffixArray;
	private final int[] beginIndices;
	private final int[] endIndices;
	private final int[] depthValues;
	
	private SuffixTree(final SuffixArray suffixArray, final int[] beginIndices, final int[] endIndices, final int[] depthValues) {
		this.suffixArray = suffixArray;
		this.beginIndices = beginIndices;
		this.endIndices = endIndices;
		this.depthValues = depthValues;
	}
	
	public int size() {
		return suffixArray.size();
	}
	
	public char[] getText() {
		return suffixArray.getText();
	}
	
	public int[] getSuffixArray() {
		return suffixArray.getIntArray();
	}
	
	public int[] getBeginIndices() {
		return beginIndices;
	}

	public int[] getEndIndices() {
		return endIndices;
	}

	public int[] getDepthValues() {
		return depthValues;
	}
	
	public static SuffixTree build(final SuffixArray suffixArray) {
		final int n = suffixArray.size();
		final char[] text = suffixArray.getText();
		final int[] SA = suffixArray.getIntArray();
		final int[] beginIndices = new int[n];
		final int[] endIndices = new int[n];
		final int[] depthValues = new int[n];
		
		
		final int[] Psi = new int[n];
		for (int i = 1; i < n; ++i)
			Psi[SA[i]] = SA[i - 1];
		
		final int[] PLCP = new int[n];
		int h = 0;
		for (int i = 0; i < n; ++i) {
			int j = Psi[i];
			while (i + h < n && j + h < n && text[i + h] == text[j + h])
				++h;
			PLCP[i] = h;
			if (h > 0)
				--h;
		}
		
		final int[] H = new int[n];
		for (int i = 0; i < n; ++i)
			H[i] = PLCP[SA[i]];
		H[0] = -1;
		
		List<Pair<Integer, Integer>> S = new ArrayList<Pair<Integer, Integer>>();
		S.add(Pair.make(-1, -1));
		int nodeNum = 0;
		for (int i = 0; ; ++i) {
			Pair<Integer, Integer> cur = Pair.make(i, (i == n) ? -1 : H[i]);
			Pair<Integer, Integer> cand = S.get(S.size() - 1);
			while (cand.getSecond() > cur.getSecond()) {
				if (i - cand.getFirst() > 1) {
					beginIndices[nodeNum] = cand.getFirst();
					endIndices[nodeNum] = i;
					depthValues[nodeNum] = cand.getSecond();
					++nodeNum;
				}
				cur = Pair.make(cand.getFirst(), cur.getSecond());
				S.remove(S.size() - 1);
				cand = S.get(S.size() - 1);
			}
			if (cand.getSecond() < cur.getSecond())
				S.add(cur);
			if (i == n)
				break;
			S.add(Pair.make(i, n - SA[i] + 1));
		}
		return new SuffixTree(suffixArray,
				ArrayUtils.subarray(beginIndices, 0, nodeNum),
				ArrayUtils.subarray(endIndices, 0, nodeNum),
				ArrayUtils.subarray(depthValues, 0, nodeNum));
	}
	
	public static SuffixTree build(final char[] text) {
		return build(SuffixArray.build(text));
	}
	
	public static SuffixTree build(final String text) {
		return build(text.toCharArray());
	}
}