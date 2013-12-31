package net.thisptr.encoder;

import java.util.List;

import net.thisptr.math.vector.SparseMapVector;
import net.thisptr.tokenizer.Tokenizer;
import net.thisptr.util.IdMapper;
import net.thisptr.util.SequentialIdMapper;

public class TextEncoder {
	private final boolean wordCount;
	private final boolean normalize;
	private final Tokenizer tokenizer;
	private final IdMapper<String> wordIdMapper;

	public TextEncoder(final Tokenizer tokenizer) {
		this(null, tokenizer, true, false);
	}

	public TextEncoder(final IdMapper<String> wordIdMapper, final Tokenizer tokenizer) {
		this(wordIdMapper, tokenizer, true, false);
	}

	public TextEncoder(final IdMapper<String> wordIdMapper, final Tokenizer tokenizer, final boolean wordCount, final boolean normalize) {
		this.tokenizer = tokenizer;
		this.wordCount = wordCount;
		this.normalize = normalize;

		this.wordIdMapper = wordIdMapper != null
				? wordIdMapper
				: new SequentialIdMapper<String>();
	}

	public int size() {
		return wordIdMapper.size();
	}

	public String getWord(final int id) {
		return wordIdMapper.reverse(id);
	}

	public Integer getWordId(final String word) {
		return wordIdMapper.get(word);
	}

	private int[] convertTokensToIds(final List<String> tokens) {
		final int[] ids = new int[tokens.size()];
		int index = 0;
		for (final String token : tokens)
			ids[index++] = wordIdMapper.map(token);
		return ids;
	}

	private static int max(final int[] values) {
		int max = -1;
		for (int i = 0; i < values.length; ++i)
			if (max < values[i])
				max = values[i];
		return max;
	}

	public SparseMapVector encode(final List<String> tokens) {
		final int[] ids = convertTokensToIds(tokens);
		final SparseMapVector result = new SparseMapVector(max(ids) + 1);
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

	public SparseMapVector encode(final String text) {
		return encode(tokenizer.tokenize(text));
	}
}