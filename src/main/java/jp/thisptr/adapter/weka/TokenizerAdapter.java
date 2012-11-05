package jp.thisptr.adapter.weka;

import java.util.Iterator;

import jp.thisptr.tokenizer.Tokenizer;

public class TokenizerAdapter extends weka.core.tokenizers.Tokenizer {
	private static final long serialVersionUID = -3624039213974905377L;
	
	private final Tokenizer tokenizer;
	private transient Iterator<String> iterator = null;
	
	public TokenizerAdapter(final Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
	
	@Override
	public String getRevision() {
		return "1.0";
	}

	@Override
	public String globalInfo() {
		return tokenizer.getClass().getSimpleName();
	}

	@Override
	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	@Override
	public Object nextElement() {
		return iterator.next();
	}

	@Override
	public void tokenize(final String text) {
		iterator = tokenizer.tokenize(text).iterator();
	}
}