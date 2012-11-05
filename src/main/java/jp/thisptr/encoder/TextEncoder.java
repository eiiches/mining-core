package jp.thisptr.encoder;

import java.util.List;

import jp.thisptr.core.util.SequencialIdMapper;
import jp.thisptr.math.vector.d.SparseMapVector;
import jp.thisptr.math.vector.d.SparseVector;
import jp.thisptr.tokenizer.Tokenizer;

public class TextEncoder {
	private final boolean wordCount;
	private final Tokenizer tokenizer;
	
	private final SequencialIdMapper<String> wordId = new SequencialIdMapper<String>();
	
	public TextEncoder(final Tokenizer tokenizer) {
		this(tokenizer, true);
	}
	
	public TextEncoder(final Tokenizer tokenizer, final boolean wordCount) {
		this.tokenizer = tokenizer;
		this.wordCount = wordCount;
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
		for (final String token : tokens) {
			Integer id = wordId.get(token);
			if (id == null)
				id = wordId.map(token);
			ids[index++] = id;
		}
		return ids;
	}
	
	public SparseVector encode(final List<String> tokens) {
		final SparseMapVector result = new SparseMapVector();
		final int[] ids = convertTokensToIds(tokens);
		if (wordCount) {
			for (final int id : ids)
				result.set(id, result.get(id) + 1);
		} else {
			for (final int id : ids)
				result.set(id, 1);
		}
		return result;
	}
	
	public SparseVector encode(final String text) {
		return encode(tokenizer.tokenize(text));
	}
}