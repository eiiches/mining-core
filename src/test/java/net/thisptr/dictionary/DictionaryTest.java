package net.thisptr.dictionary;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.thisptr.dictionary.Dictionary;

import org.junit.Test;

public class DictionaryTest {

	@Test
	public void test() throws IOException {
		Dictionary d = Dictionary.load(Arrays.asList("hoge", "fuga", "piyo", "piyofuga"));
		List<String> actual = d.matcher("piyofuga").findAll(String.class);
		List<String> expected = Arrays.asList("piyo", "piyofuga", "fuga");
		Collections.sort(actual);
		Collections.sort(expected);
		assertArrayEquals(expected.toArray(), actual.toArray());
	}
	
}
