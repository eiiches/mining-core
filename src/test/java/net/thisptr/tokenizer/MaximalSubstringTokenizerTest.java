package net.thisptr.tokenizer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class MaximalSubstringTokenizerTest {

	@Test
	public void test() {
		// Maximal Substrings: (-_-#), (, ), *, -, ^, な, 中途半端な, 天気
		final List<String> tweets = Arrays.asList(
				"おはよー！天気良いね(*^^*)",
				"中途半端な天気だなぁ(-_-#)",
				"中途半端な時間に起きちゃった(-_-#)"
		);
		final Tokenizer tokenizer = new MaximalSubstringTokenizer(tweets);
		final List<String> tokens = tokenizer.tokenize("中途半端な実装になってしまった(-_-#)");
		final List<String> expected = Arrays.asList("中途半端な", "な", "な", "(", "(-_-#)", "-", "-", ")");
		Collections.sort(tokens);
		Collections.sort(expected);
		assertEquals(expected, tokens);
	}
}
