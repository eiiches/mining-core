package net.thisptr.string.suffixarray;

import net.thisptr.lang.sequence.Sequence;


public abstract class SuffixArrayBuilder {
	public abstract SuffixArray build(final char[] text);
	public abstract SuffixArray build(final Sequence seq);
}