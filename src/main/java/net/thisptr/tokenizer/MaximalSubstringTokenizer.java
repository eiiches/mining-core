package net.thisptr.tokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.thisptr.dictionary.Dictionary;
import net.thisptr.structure.suffixarray.SuffixArray;
import net.thisptr.structure.suffixarray.SuffixTree;
import net.thisptr.structure.suffixarray.algorithm.MaximalSubstring;

public class MaximalSubstringTokenizer extends Tokenizer {
	private final Dictionary dictionary;
	private final DictionaryTokenizer tokenizer;
	
	public MaximalSubstringTokenizer(final Iterable<String> learnset) {
		final StringBuilder builder = new StringBuilder();
		for (final String learn : learnset) {
			builder.append(learn);
			builder.append((char) 0);
		}
		final String source = builder.toString();
		final SuffixArray suffixArray = SuffixArray.build(source);
		final SuffixTree suffixTree = SuffixTree.build(suffixArray);
		
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
		
		try {
			dictionary = Dictionary.load(new ArrayList<String>(fixed));
			tokenizer = new DictionaryTokenizer(dictionary);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	@Override
	public List<String> tokenize(final String text) {
		return tokenizer.tokenize(text);
	}
}
