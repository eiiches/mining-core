package net.thisptr.string.suffixarray;

import net.thisptr.lang.sequence.CharArraySequence;
import net.thisptr.lang.sequence.Sequence;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;

public final class NaiveSuffixArrayBuilder extends SuffixArrayBuilder {
	public SuffixArray build(final char[] text) {
		return build(new CharArraySequence(text));
	}
	
	public SuffixArray build(final Sequence seq) {
		final int[] sa = new int[seq.length()];
		for (int i = 0; i < sa.length; ++i)
			sa[i] = i;
		
		// IntArrays.quickSort often throws StackOverflowError.
		IntArrays.mergeSort(sa, new IntComparator() {
			@Override
			public int compare(final Integer o1, final Integer o2) {
				return compare(o1.intValue(), o2.intValue());
			}
			@Override
			public int compare(int i1, int i2) {
				while (true) {
					if (i1 >= seq.length()) return -1;
					if (i2 >= seq.length()) return 1;
					if (seq.intValue(i1) != seq.intValue(i2))
						return seq.intValue(i1) - seq.intValue(i2);
					++i1; ++i2;
				}
			}
		});
		
		return new SuffixArray(sa);
	}
}