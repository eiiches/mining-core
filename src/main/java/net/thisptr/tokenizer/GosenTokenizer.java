package net.thisptr.tokenizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.java.sen.SenFactory;
import net.java.sen.StringTagger;
import net.java.sen.dictionary.Token;

public class GosenTokenizer extends Tokenizer {
	private final StringTagger tagger;
	private final Pattern includePos;
	
	public GosenTokenizer(final File dictionaryDir) {
		this(dictionaryDir, null);
	}
	
	public GosenTokenizer(final File dictionaryDir, final Pattern includePos) {
		this.tagger = SenFactory.getStringTagger(dictionaryDir != null ? dictionaryDir.getPath() : null);
		this.includePos = includePos;
	}

	@Override
	public List<String> tokenize(final String text) {
		final List<Token> tokens = new ArrayList<Token>();
		try {
			tagger.analyze(text, tokens);
		} catch (IOException e) {
			throw new RuntimeException("Error tokenizing text", e);
		}
		
		final List<String> result = new ArrayList<String>(tokens.size());
		for (final Token token : tokens)
			if (includePos == null || includePos.matcher(token.getMorpheme().getPartOfSpeech()).find())
				result.add(token.getSurface());
		return result;
	}
}