package net.thisptr.structure.suffixarray.builder;

import java.util.Arrays;
import java.util.Comparator;

import net.thisptr.structure.suffixarray.SuffixArray;

import org.apache.commons.lang3.ArrayUtils;

public final class SuffixArrayBuilderNaive extends SuffixArrayBuilder {
	public SuffixArray build(final char[] text) {
		final Integer[] suffixArray = new Integer[text.length];
		for (int i = 0; i < suffixArray.length; ++i)
			suffixArray[i] = i;
		Arrays.sort(suffixArray, new Comparator<Integer>() {
			@Override
			public int compare(final Integer o1, final Integer o2) {
				int i1 = o1, i2 = o2;
				assert i1 != i2;
				while (true) {
					if (text[i1] < text[i2])
						return -1;
					if (text[i1] > text[i2])
						return 1;
					++i1; ++i2;
					if (i1 >= text.length)
						return -1;
					if (i2 >= text.length)
						return 1;
				}
			}
		});
		return new SuffixArray(text, ArrayUtils.toPrimitive(suffixArray));
	}
}