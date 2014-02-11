package net.thisptr.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NGramTokenizer extends Tokenizer {
	private final int n;
	private final int m;

	public NGramTokenizer() {
		this(2);
	}

	public NGramTokenizer(final int n) {
		this.n = n;
		this.m = n;
	}

	/**
	 * Construct [n, m]-inclusive-grams tokenizer.
	 * 
	 * @param n
	 * @param m
	 */
	public NGramTokenizer(final int n, final int m) {
		this.n = n;
		this.m = m;
	}

	public List<String> tokenize(final char[] text) {
		List<String> result = new ArrayList<String>();
		for (int r = n; r <= m; ++r)
			for (int i = 0; i < text.length - r + 1; ++i)
				result.add(new String(Arrays.copyOfRange(text, i, i + r)));
		return result;
	}

	@Override
	public List<String> tokenize(final String text) {
		return tokenize(text.toCharArray());
	}
}
