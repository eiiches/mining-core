package jp.thisptr.structure.suffixarray.builder;

import jp.thisptr.structure.suffixarray.SuffixArray;

public final class SuffixArrayBuilderSAIS {
	private static final int k = Character.MAX_VALUE + 1;
	
	private static int[] getCounts(final char[] text) {
		final int[] result = new int[k];
		for (final char ch : text)
			++result[ch];
		return result;
	}
	
	private static int[] getBucketIndices(final int[] counts) {
		int index = 0;
		final int[] indices = new int[k];
		for (int i = 0; i < k; ++i) {
			indices[i] = index;
			index += counts[i];
		}
		return indices;
	}
	
	private static int[] getBucketEndIndices(final int[] counts) {
		int index = 0;
		final int[] indices = new int[k];
		for (int i = 0; i < k; ++i) {
			index += counts[i];
			indices[i] = index;
		}
		return indices;
	}
	
	private static void induceSA(final char[] text, final int[] SA) {
		final int n = text.length;
		final int[] counts = getCounts(text);
		
		/* induce L-type suffix array */ {
			final int[] bucketIndices = getBucketIndices(counts);
			int j = text.length - 1;
			int c1 = text[j];
			int c0 = text[j - 1];
			int b = bucketIndices[c1];
			SA[b++] = (0 < j && c0 < c1) ? ~j : j;
			for (int i = 0; i < n; ++i) {
				j = SA[i];
				SA[i] = ~j;
				if (0 < j) {
					c0 = text[--j];
					if (c0 != c1) {
						bucketIndices[c1] = b;
						b = bucketIndices[c0];
					}
					c1 = text[j];
					c0 = text[j - 1];
					SA[b++] = (0 < j && c0 < c1) ? ~j : j;
				}
			}
		}
			
		/* induce S-type suffix array */ {
			final int[] bucketEndIndices = getBucketEndIndices(counts);
			int c1 = 0;
			int b = bucketEndIndices[0];
			for (int i = n - 1; 0 <= i; --i) {
				int j = SA[i];
				if (0 < j) {
					int c0 = text[--j];
					if (c0 != c1) {
						bucketEndIndices[c1] = b;
						b = bucketEndIndices[c0];
					}
					c1 = c0;
					SA[--b] = ((j == 0) || (text[j-1] > c1)) ? ~j : j;
				} else {
					SA[i] = ~j;
				}
			}
		}
	}
	
	private static abstract class IndexVisitor {
		public abstract void visit(int index);
	}
	
	/**
	 * Scan input text to find all the indices of the Left-most S-type (LMS) characters.
	 * @param text
	 * @param visitor
	 */
	private static void forEachLMS(final char[] text, final IndexVisitor visitor) {
		boolean isSType = false;
		for (int i = text.length - 2; 0 <= i; --i) {
			char c0 = text[i];
			char c1 = text[i + 1];
			if (c0 > c1) {
				if (isSType) /* then c1(=text[i+1]) is a LMS character. */
					visitor.visit(i);
				isSType = false;
			} else if (c0 < c1) {
				isSType = true;
			}
		}
	}

	private static int[] buildSuffixArray(final char[] text) {
		final int n = text.length;
		final int[] suffixArray = new int[n];
		
		/* stage 1:
		 * reduce the problem by at least 1/2.
		 */
		
		/* sort all the S-substrings */ {
			final int[] bucketEndIndices = getBucketEndIndices(getCounts(text));
			forEachLMS(text, new IndexVisitor() {
				public void visit(final int index) {
					suffixArray[--bucketEndIndices[text[index + 1]]] = index + 1;
				}
			});
			induceSA(text, suffixArray);
		}
		
		/* compact all the sorted substrings into the first m items of SA */
		/* 2*m must be not larger than n */
		int _nLMS = 0;
		for (int i = 0; i < n; ++i) {
			if (isLMS(text, suffixArray[i]))
				suffixArray[_nLMS++] = suffixArray[i];
		}
		final int nLMS = _nLMS;
		
		/* initialize the name array buffer */
		for (int i = nLMS; i < nLMS + n / 2; ++i)
			suffixArray[i] = 0;
		
		/* store the length of all substring */
		forEachLMS(text, new IndexVisitor() {
			private int lastLMSIndex = n;
			public void visit(final int index) {
				suffixArray[nLMS + (index + 1) / 2] = lastLMSIndex - index - 1;
				lastLMSIndex = index + 1;
			}
		});

		int nName = 0;
		/* find the lexicographic names of all substrings */ {
			int q = n;
			int qlen = 0;
			for (int i = 0; i < nLMS; ++i) {
		        int p = suffixArray[i];
		        int plen = suffixArray[nLMS + (p >> 1)];
		        if (!isStringEqual(text, p, plen, q, qlen)) {
		        	++nName;
		        	q = p;
		        	qlen = plen;
	        	}
		        suffixArray[nLMS + (p >> 1)] = nName;
			}
		}
		
		/* stage 2:
		 * solve the reduced problem,
		 * recurse if names are not yet unique.
		 */
		
		if (nName < nLMS) { /* there are duplicate LMS-substrings. */
	        RA = suffixArray + n + fs - nLMS;
	        
	        int j = nLMS - 1;
	        for (int i = nLMS + (n >> 1) - 1; nLMS <= i; --i) {
	            if (suffixArray[i] != 0)
	            	RA[j--] = suffixArray[i] - 1;
	        }
	        
	        suffixsort(RA, suffixArray, fs + n - nLMS * 2, nLMS, nName);
	        
	        forEachLMS(text, new IndexVisitor() {
	        	private int j = nLMS - 1;
				public void visit(final int index) {
					RA[j--] = index + 1;
				}
			});
	        
	        for (int i = 0; i < nLMS; ++i) {
	        	/* get index in s */
	        	suffixArray[i] = RA[suffixArray[i]];
        	}
		}
		
		/* stage 3:
		 * Induce the result for the original problem.
		 */
		
        /* put all left-most S characters into their buckets */ {
			final int[] counts = getCounts(text);
			final int[] bucketEndIndices = getBucketEndIndices(counts);
	        for (int i = nLMS; i < n; ++i) { suffixArray[i] = 0; } /* init SA[m..n-1] */
	        for (int i = nLMS - 1; 0 <= i; --i) {
	            int j = suffixArray[i];
	            suffixArray[i] = 0;
	            suffixArray[--bucketEndIndices[text[j]]] = j;
	        }
	        induceSA(text, suffixArray);
        }
	}
	
	private static boolean isStringEqual(final char[] text, final int pIndex, final int pLength, final int qIndex, final int qLength) {
		if (qLength != pLength)
			return false;
		return isStringEqual(text, pIndex, qIndex, pLength);
	}
	
	private static boolean isStringEqual(final char[] text, final int pIndex, final int qIndex, final int length) {
		for (int i = 0; i < length; ++i) {
			if (text[pIndex + i] != text[qIndex + i])
				return false;
		}
		return true;
	}
	
	private static boolean isLMS(final char[] text, final int index) {
		/* suf(text, 0) cannot be LMS */
		if (index == 0)
			return false;
		int c0 = text[index];
		if (text[index - 1] > c0) {
			int j;
			int c1 = text[index + 1];
			for (j = index + 1; j < text.length; ++j) {
				c1 = text[j];
				if (c0 != c1)
					break;
			}
			if (j < text.length && c0 < c1)
				return true;
		}
		return false;
	}
	
	public static SuffixArray build(final char[] text) {
		final int[] suffixArray = buildSuffixArray(text);
		return new SuffixArray(text, suffixArray);
	}
}