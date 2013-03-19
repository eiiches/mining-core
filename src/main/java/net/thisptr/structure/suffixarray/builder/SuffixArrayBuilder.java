package net.thisptr.structure.suffixarray.builder;

import net.thisptr.structure.suffixarray.SuffixArray;

public abstract class SuffixArrayBuilder {
	public abstract SuffixArray build(final char[] text);
}