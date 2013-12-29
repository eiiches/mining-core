package net.thisptr.encoder;

import java.util.List;

import net.thisptr.math.vector.SparseMapVector;
import net.thisptr.math.vector.SparseVector;
import net.thisptr.tokenizer.Tokenizer;
import net.thisptr.util.SequencialIdMapper;

public class TextEncoder {
	private final boolean wordCount;
	private final boolean normalize;
	private final Tokenizer tokenizer;

	private final SequencialIdMapper<String> wordId = new SequencialIdMapper<String>();

	public TextEncoder(final Tokenizer tokenizer) {
		this(tokenizer, true, false);
	}

	public TextEncoder(final Tokenizer tokenizer, final boolean wordCount, final boolean normalize) {
		this.tokenizer = tokenizer;
		this.wordCount = wordCount;
		this.normalize = normalize;
	}

	public int size() {
		return wordId.size();
	}

	public String getWord(final int id) {
		return wordId.reverse(id);
	}

	public Integer getWordId(final String word) {
		return wordId.get(word);
	}

	private int[] convertTokensToIds(final List<String> tokens) {
		final int[] ids = new int[tokens.size()];
		int index = 0;
		for (final String token : tokens)
			ids[index++] = wordId.map(token);
		return ids;
	}

	public SparseVector encode(final List<String> tokens) {
		final SparseMapVector result = new SparseMapVector();
		final int[] ids = convertTokensToIds(tokens);
		final double incr = normalize ? 1.0 / ids.length : 1.0;
		if (wordCount) {
			for (final int id : ids)
				result.set(id, result.get(id) + incr);
		} else {
			for (final int id : ids)
				result.set(id, incr);
		}
		return result;
	}

	public SparseVector encode(final String text) {
		return encode(tokenizer.tokenize(text));
	}
}