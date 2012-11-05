package jp.thisptr.tokenizer;

import java.util.List;

public abstract class Tokenizer {
	public abstract List<String> tokenize(final String text);
}