package net.thisptr.string.misc;

import net.thisptr.lang.sequence.CharArraySequence;
import net.thisptr.lang.sequence.Sequence;
import net.thisptr.string.suffixarray.SaisSuffixArrayBuilder;
import net.thisptr.string.suffixarray.SuffixArray;

public class BurrowsWheelerTransform {
	
	public char[] transform(final char[] seq, final SuffixArray sa) {
		final CharArraySequence in = new CharArraySequence(seq);
		return ((CharArraySequence) transform(in, sa)).charArray();
	}
	
	public Sequence transform(final Sequence seq, final SuffixArray sa) {
		assert seq.length() == sa.length();
		final int n = seq.length();
		
		final Sequence result = seq.copy();
		for (int i = 0; i < n; ++i) {
			final int bwi = sa.intValue(i) - 1;
			if (bwi < 0) {
				result.set(i, seq.intValue(n - 1));
			} else {
				result.set(i, seq.intValue(bwi));
			}
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		final char[] in = "あなたとジャバ".toCharArray();
		final char[] bwt = new BurrowsWheelerTransform().transform(in, 
				new SaisSuffixArrayBuilder().build(in));
		System.out.println(new String(bwt));
	}
}
