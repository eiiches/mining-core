package net.thisptr.dictionary;

import java.util.ArrayList;
import java.util.List;

public class Matcher {
	
	private Dictionary dictionary;
	private String input;
	
	public Matcher(final Dictionary dictionary, final String input) {
		this.dictionary = dictionary;
		this.input = input;
	}
	
	public List<Integer> findAll() {
		List<Integer> result = new ArrayList<Integer>();
		StringIterator inputIterator = new StringIterator(input);
		while (inputIterator.hasNext()) {
			result.addAll(dictionary.commonPrefixSearch(inputIterator));
			inputIterator.next();
		}
		return result;
	}
	
	public List<String> findAll(final Class<String> dummy) {
		String[] wordArray = dictionary.getWordArray();
		List<String> result = new ArrayList<String>();
		for (int id : findAll()) {
			result.add(wordArray[id]);
		}
		return result;
	}
}
