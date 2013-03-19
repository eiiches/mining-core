package net.thisptr.tokenizer;

import java.util.ArrayList;
import java.util.List;

import net.thisptr.dictionary.Dictionary;

public class DictionaryTokenizer extends Tokenizer {
	private final Dictionary dictionary;
	
	public DictionaryTokenizer(final Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	@Override
	public List<String> tokenize(final String text) {
		final List<String> result = new ArrayList<String>();
		final String[] wordArray = dictionary.getWordArray();
		for (final int wordId : dictionary.matcher(text).findAll())
			result.add(wordArray[wordId]);
		return result;
	}

}
