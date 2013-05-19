package net.thisptr.tokenizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.thisptr.string.matcher.StringPattern;
import net.thisptr.string.matcher.WuManberPattern;
import net.thisptr.string.misc.MaximalSubstring;
import net.thisptr.string.misc.SuffixTree;
import net.thisptr.string.suffixarray.SaisSuffixArrayBuilder;
import net.thisptr.string.suffixarray.SuffixArray;

public class MaximalSubstringTokenizer extends Tokenizer {
	private final StringPattern dictionary;
	private final DictionaryTokenizer tokenizer;
	private final List<String> maximalSubstrings;
	
	public MaximalSubstringTokenizer(final Iterable<String> learnset) {
		final StringBuilder builder = new StringBuilder();
		for (final String learn : learnset) {
			builder.append(learn);
			builder.append((char) 0);
		}
		final String source = builder.toString();
		final char[] sourceCharArray = source.toCharArray();
		final SuffixArray suffixArray = new SaisSuffixArrayBuilder().build(sourceCharArray);
		final SuffixTree suffixTree = SuffixTree.build(sourceCharArray, suffixArray);
		
		final Set<String> fixed = new HashSet<String>();
		final List<String> substrings = new MaximalSubstring(suffixTree).extractAll();
		for (final String substring : substrings) {
			final int sep = substring.indexOf(0);
			if (sep < 0) {
				fixed.add(substring);
				continue;
			}
			if (sep - 0 > 0)
				fixed.add(substring.substring(0, sep));
			if (substring.length() - sep - 1 > 0)
				fixed.add(substring.substring(sep + 1, substring.length()));
		}
		
		maximalSubstrings = new ArrayList<String>(fixed);
		dictionary = WuManberPattern.compile(new ArrayList<String>(fixed));
		tokenizer = new DictionaryTokenizer(dictionary);
	}

	public List<String> getMaximalSubstrings() {
		return maximalSubstrings;
	}

	@Override
	public List<String> tokenize(final String text) {
		return tokenizer.tokenize(text);
	}
}
