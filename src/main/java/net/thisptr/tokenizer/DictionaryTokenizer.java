package net.thisptr.tokenizer;

import java.util.ArrayList;
import java.util.List;

import net.thisptr.string.matcher.StringMatcher;
import net.thisptr.string.matcher.StringPattern;

public class DictionaryTokenizer extends Tokenizer {
	private final StringPattern dictionary;
	
	public DictionaryTokenizer(final StringPattern dictionary) {
		this.dictionary = dictionary;
	}

	@Override
	public List<String> tokenize(final String text) {
		final List<String> result = new ArrayList<String>();
		final StringMatcher matcher = dictionary.matcher(text);
		while (matcher.find())
			result.add(matcher.text());
		return result;
	}
}
