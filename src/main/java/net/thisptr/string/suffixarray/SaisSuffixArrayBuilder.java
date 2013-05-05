package net.thisptr.string.suffixarray;


import java.util.Arrays;

import net.thisptr.lang.sequence.CharArraySequence;
import net.thisptr.lang.sequence.IntArraySequence;
import net.thisptr.lang.sequence.Sequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of SA-IS, a suffix array construction algorithm, as described in,
 * Ge Nong, "Two Efficient Algorithms for Linear Time Suffix Array Construction,"
 * IEEE Transactions on Computers, vol. 60, no. 10, Oct. 2011.
 * 
 * Also some optimization have been used from, [Nong 11, OSACA] Ge Nong, "An Optimal Suffix Array Construction Algorithm,"
 * Technical Report, Department of Computer Science, Sun Yat-sen University, 2011.
 * 
 */
public final class SaisSuffixArrayBuilder extends SuffixArrayBuilder {
	private static Logger log = LoggerFactory.getLogger(SaisSuffixArrayBuilder.class);
	private static final int EMPTY = -1;
	
	private static boolean isS(final Sequence seq, final int index) {
		final int n = seq.length();
		
		int c0 = seq.intValue(index);
		for (int i = index; i < n - 1; ++i) {
			final int c1 = seq.intValue(i + 1);
			if (c0 < c1)
				return true;
			if (c0 > c1)
				return false;
			c0 = c1;
		}
		
		return false;
	}
	
	/**
	 * Return whether <tt>text[index]</tt> is a LMS character.
	 * A character <tt>text[index]</tt> is called LMS, if <tt>text[i]</tt> is S-type and <tt>text[index - 1]</tt> is L-type.
	 * @param text
	 * @param index
	 * @return
	 */
	private static boolean isLms(final Sequence seq, final int index) {
		if (index == 0)
			return false; // by definition
		
		// return isS(seq, index) && !isS(seq, index - 1);
		return isS(seq, index) && seq.intValue(index - 1) > seq.intValue(index);
	}
	
	/**
	 * @param seq
	 * @param alphabetSize
	 * @return
	 */
	private static int[] buildCountArray(final Sequence seq, final int alphabetSize) {
		final int n = seq.length();
		final int[] result = new int[alphabetSize];
		for (int i = 0; i < n; ++i) {
			final int ch = seq.intValue(i);
			++result[ch];
		}
		return result;
	}

	private static int[] buildBucketIndices(final int[] counts) {
		int index = 0;
		final int[] indices = new int[counts.length];
		for (int i = 0; i < counts.length; ++i) {
			indices[i] = index;
			index += counts[i];
		}
		return indices;
	}
	
	private static int[] buildBucketEndIndices(final int[] counts) {
		int index = 0;
		final int[] indices = new int[counts.length];
		for (int i = 0; i < counts.length; ++i) {
			index += counts[i];
			indices[i] = index;
		}
		return indices;
	}
	
	private interface IndexVisitor {
		void visit(int index);
	}
	
	private static void forEachLmsReversed(final Sequence seq, final IndexVisitor visitor) {
		forEachLmsReversed(seq, visitor, false);
	}
	
	private static void forEachLmsReversed(final Sequence seq, final IndexVisitor visitor, final boolean includeSentinel) {
		if (includeSentinel)
			visitor.visit(seq.length());
		
		boolean isS = false;
		for (int i = seq.length() - 2; 0 <= i; --i) {
			final int c0 = seq.intValue(i);
			final int c1 = seq.intValue(i + 1);
			if (c0 > c1) {
				if (isS)
					visitor.visit(i + 1);
				isS = false;
			} else if (c0 < c1) {
				isS = true;
			}
		}
	}
	
	/**
	 * @param text
	 * @param counts
	 * @param suffixArray A suffix array (with L-types sorted) to induce sort. This array will be modified directly.
	 */
	private static void induceSortS(final Sequence seq, final int[] counts, final Sequence sa) {
		final int n = sa.length();
		final int[] bucketEndIndices = buildBucketEndIndices(counts);
		
		for (int i = n - 1; i >= 0; --i) {
			final int sai = sa.intValue(i);
			if (sai <= 0)
				continue;

			// suf(S, SA[i] - 1) is S-type iff,
			//   (1). S[SA[i] - 1] < S[SA[i]] or,
			//   (2). S[SA[i] - 1] == S[SA[i]] and bucketEndIndices[S[SA[i] - 1]] <= i
			// see details for section 3 of [Nong 11, OSACA] for this optimization
			final int ssai0 = seq.intValue(sai - 1);
			final int ssai1 = seq.intValue(sai);
			if (ssai0 < ssai1 || ssai0 == ssai1 && bucketEndIndices[ssai0] <= i) // if (isS(seq, sai - 1))
				sa.set(--bucketEndIndices[ssai0], sai - 1);
		}
		
		log.debug("induceSortS(): end, sa = {}", sa);
	}
	
	/**
	 * @param text
	 * @param counts
	 * @param suffixArray A suffix array to induce sort. This array will be modified directly.
	 */
	private static void induceSortL(final Sequence seq, final int[] counts, final Sequence sa) {
		final int n = seq.length();
		final int[] bucketIndices = buildBucketIndices(counts);

		// suffixArray[-1] is the sentinel '$'.
		// if (isL(seq, seq.length() - 1)) which is always true, then
		sa.set(bucketIndices[seq.intValue(n - 1)]++, n - 1);
		
		for (int i = 0; i < n; ++i) {
			final int sai = sa.intValue(i);
			if (sai <= 0)
				continue;

			// see section 3 of [Nong 11, OSACA] for this optimization
			// if (!isS(seq, sa.intValue(i) - 1))
			final int ssai0 = seq.intValue(sai - 1);
			final int ssai1 = seq.intValue(sai);
			if (ssai0 >= ssai1)
				sa.set(bucketIndices[ssai0]++, sai - 1);
		}
		
		log.debug("induceSortL(): end, sa = {}", sa);
	}

	private static void sais(final Sequence seq, final Sequence sa, final int alphabetSize) {
		log.info("sais(): satrt, n = {}", seq.length());
		log.debug("sais(): start, n = {}, seq = {}", seq.length(), seq);
		
		final int n = seq.length();
		if (n == 0) {
			log.debug("sais(): len(s) == 0, end");
			return;
		}
		
		sa.fill(EMPTY);
		
		/* stage 1:
		 * reduce the problem by at least 1/2.
		 */

		final int[] counts = buildCountArray(seq, alphabetSize);

		/* Step-1: */ {
			final int[] bucketEndIndices = buildBucketEndIndices(counts);
			forEachLmsReversed(seq, new IndexVisitor() {
				public void visit(final int index) {
					sa.set(--bucketEndIndices[seq.intValue(index)], index);
				}
			});
		}
		log.debug("lms into bucket: sa = {}", sa);

		/* Step-2: induce sort all the L-type LMS-prefixes */
		induceSortL(seq, counts, sa);
		/* Step-3: induce sort all the LMS-prefixes from the sorted L-type prefixes */
		induceSortS(seq, counts, sa);
		
		/* compact all the sorted substrings into the first m items of SA */
		/* 2*m must be not larger than n */

		int _n1 = 0;
		for (int i = 0; i < n; ++i) {
			if (isLms(seq, sa.intValue(i)))
				sa.set(_n1++, sa.intValue(i));
		}
		final int n1 = _n1;
		
		if (n1 == 0) {
			log.debug("sais(): len(s1) == 0, end");
			return;
		}
		
		/* initialize the name array buffer */
		sa.view(n1, n1 + n / 2).fill(-1);
		
		log.debug("compact lms chars: sa = {}", sa);
		log.debug("reduced problem is: length = {}", n1);

		/* store the length of all substring */
		forEachLmsReversed(seq, new IndexVisitor() {
			private int nextIndex = n;
			@Override
			public void visit(final int index) {
				sa.set(n1 + index / 2, nextIndex - index + 1);
				nextIndex = index;
			}
		}, true);

		log.debug("lengths stored: sa = {}", sa);
		
		int nNames = 0;
		/* find the lexicographic names of all substrings */ {
			int pBegin = n;
			int pLength = 0;
			for (int i = 0; i < n1; ++i) {
		        int begin = sa.intValue(i);
		        int length = sa.intValue(n1 + begin / 2);
		        if (!isSequenceEqual(seq, pBegin, pLength, begin, length)) {
		        	++nNames;
		        	pBegin = begin;
		        	pLength = length;
	        	}
		        sa.set(n1 + begin / 2, nNames - 1);
			}
		}
		
		log.debug("id's assigned: sa = {}", sa);
		
		// move over names to left into range [n1, 2 * n1).
		for (int i = n1, j = n1; i < n1 + n / 2; ++i)
			if (sa.intValue(i) != EMPTY)
				sa.set(j++, sa.intValue(i));
		
		log.debug("id's compacted: sa = {}", sa);
		log.debug("reduced problem is: alphabetSize = {}", nNames);
		
		/* stage 2:
		 * solve the reduced problem,
		 * recurse if names are not yet unique. in this recursion eventually names will be unique.
		 */
		
		if (nNames < n1) {
			// names are not unique.
			sais(sa.view(n1, n1 + n1), sa.view(0, n1), nNames);
		} else {
			// since names are unique, it is easy to build suffix array for LMS-substrings names.
			for (int i = 0; i < n1; ++i)
				sa.set(sa.intValue(n1 + i), i);
		}
		
		if (log.isDebugEnabled())
			log.debug("reduced problem: sa = {}", sa.view(0, n1));
		
		/* stage 3:
		 * Induce the result for the original problem.
		 */
		
		forEachLmsReversed(seq, new IndexVisitor() {
			private int j = n1;
			@Override
			public void visit(final int index) {
				sa.set(n1 + --j, index);
			}
		});

		log.debug("unsorted lms: sa = {}", sa);

		for (int i = 0; i < n1; ++i)
			sa.set(i, sa.intValue(sa.intValue(i) + n1));

		log.debug("sort lms: sa = {}", sa);

		sa.view(n1, n).fill(EMPTY);

		log.debug("clear space: sa = {}", sa);

		/* sorted prefixes into their bucket */ {
			final int[] bucketEndIndices = buildBucketEndIndices(counts);
			for (int i = n1 - 1; i >= 0; --i) {
				final int tmp = sa.intValue(i);
				sa.set(i, EMPTY);
				sa.set(--bucketEndIndices[seq.intValue(tmp)], tmp);
			}
		}
		
		log.debug("lms into bucket: sa = {}", sa);

		induceSortL(seq, counts, sa);
		induceSortS(seq, counts, sa);
		
		log.debug("sais(): end");
		log.info("sais(): end, n = {}", n);
	}
	
	private static boolean isSequenceEqual(final Sequence seq, final int leftBegin, final int leftLength, final int rightBegin, final int rightLength) {
		if (rightLength != leftLength)
			return false;
		
		return isSequenceEqual(seq, leftBegin, rightBegin, leftLength);
	}
	
	private static boolean isSequenceEqual(final Sequence seq, final int leftBegin, final int rightBegin, final int length) {
		if (rightBegin + length >= seq.length()
				|| leftBegin + length >= seq.length())
			return false;
		
		int leftIndex = leftBegin;
		int rightIndex = rightBegin;
		
		for (int i = 0; i < length; ++i)
			if (seq.intValue(leftIndex++) != seq.intValue(rightIndex++))
				return false;
		
		return true;
	}
	
	private SuffixArray build(final Sequence seq, final int alphabetSize) {
		log.debug("build(): start");
		final IntArraySequence sa = new IntArraySequence(seq.length());
		sais(seq, sa, alphabetSize);
		return new SuffixArray(sa.intArray());
	}
	
	public SuffixArray build(final Sequence seq) {
		int maxValue = 0;
		for (int i = 0; i < seq.length(); ++i) {
			if (seq.intValue(i) < 0)
				throw new IllegalArgumentException("All values in the sequence must be positive.");
			if (maxValue < seq.intValue(i))
				maxValue = seq.intValue(i);
		}
		return build(seq, maxValue + 1);
	}

	public SuffixArray build(final char[] text) {
		return build(new CharArraySequence(text), Character.MAX_VALUE + 1);
	}
	
	private static void describe(final String text) {
		System.out.printf("input = %s%n", Arrays.toString(text.toCharArray()));
		final char[] types = new char[text.length()];
		for (int i = 0; i < text.length(); ++i)
			types[i] = isS(new CharArraySequence(text.toCharArray()), i) ? 'S' : 'L';
		System.out.printf("types = %s%n", Arrays.toString(types));
		final char[] lms = new char[text.length()];
		for (int i = 0; i < text.length(); ++i)
			lms[i] = isLms(new CharArraySequence(text.toCharArray()), i) ? '*' : ' ';
		System.out.printf("lms   = %s%n", Arrays.toString(lms));
	}
	
	public static void main(final String[] args) {
		final String input = "mmiissiissiippii";
		describe(input);
		System.out.println();
		
		new SaisSuffixArrayBuilder().build(input.toCharArray());
	}
}