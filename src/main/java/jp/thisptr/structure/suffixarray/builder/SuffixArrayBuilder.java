package jp.thisptr.structure.suffixarray.builder;

import jp.thisptr.structure.suffixarray.SuffixArray;

public abstract class SuffixArrayBuilder {
	public abstract SuffixArray build(final char[] text);
}