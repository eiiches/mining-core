package net.thisptr.structure.suffixarray;

import net.thisptr.structure.suffixarray.builder.SuffixArrayBuilder;
import net.thisptr.structure.suffixarray.builder.SuffixArrayBuilderNaive;

public class SuffixArray {
	private final char[] text;
	private final int[] intArray;
	
	public SuffixArray(final char[] text, final int[] sa) {
		this.text = text;
		this.intArray = sa;
	}
	
	public int size() {
		return text.length;
	}
	
	public char[] getText() {
		return text;
	}
	
	public int[] getIntArray() {
		return intArray;
	}
	
	public static SuffixArray build(final char[] text, final SuffixArrayBuilder builder) {
		return builder.build(text);
	}
	public static SuffixArray build(final char[] text) {
		return build(text, new SuffixArrayBuilderNaive());
	}
	public static SuffixArray build(final String text, final SuffixArrayBuilder builder) {
		return build(text.toCharArray(), builder);
	}
	public static SuffixArray build(final String text) {
		return build(text.toCharArray());
	}
}