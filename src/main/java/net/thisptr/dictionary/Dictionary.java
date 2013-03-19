package net.thisptr.dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.java.sen.trie.TrieBuilder;
import net.java.sen.trie.TrieSearcher;

import org.apache.commons.io.FileUtils;

public final class Dictionary {
	private IntBuffer trieBuffer;
	private String[] wordArray;
	
	private Dictionary(final IntBuffer trieBuffer, final String[] wordArray) {
		this.trieBuffer = trieBuffer;
		this.wordArray = wordArray;
	}
	
	public static Dictionary load(final List<String> words) throws IOException {
		List<String> copyOfWords = new ArrayList<String>(words);
		Collections.sort(copyOfWords);
		String[] wordArray = copyOfWords.toArray(new String[copyOfWords.size()]);
		int[] sequencialIds = new int[wordArray.length];
		for (int i = 0; i < wordArray.length; ++i)
			sequencialIds[i] = i;
		
		File trieFile = File.createTempFile(Dictionary.class.getCanonicalName() + ".", ".trie");
		trieFile.deleteOnExit();
		
		TrieBuilder trieBuilder = new TrieBuilder(wordArray, sequencialIds, wordArray.length);
		trieBuilder.build(trieFile.getAbsolutePath());
		
		try (FileInputStream trieFileStream = new FileInputStream(trieFile)) {
			FileChannel trieFileChannel = trieFileStream.getChannel();
			IntBuffer trieBuffer = trieFileChannel.map(FileChannel.MapMode.READ_ONLY, 0, trieFileChannel.size()).asIntBuffer();
			while (trieBuffer.hasRemaining())
				trieBuffer.get();
			return new Dictionary(trieBuffer, wordArray);
		}
	}
	
	public static Dictionary loadFile(final File dictionaryFile) throws IOException {
		return load(FileUtils.readLines(dictionaryFile));
	}
	
	public static Dictionary loadFile(final String dictionaryFile) throws IOException {
		return loadFile(new File(dictionaryFile));
	}
	
	public Matcher matcher(final String input) {
		return new Matcher(this, input);
	}
	
	public IntBuffer getTrieBuffer() {
		return trieBuffer;
	}
	
	public String[] getWordArray() {
		return wordArray;
	}
	
	public List<Integer> commonPrefixSearch(final StringIterator iterator) {
		StringIterator copyOfIterator = new StringIterator(iterator);
		List<Integer> result = new ArrayList<Integer>();
		int[] resultArray = new int[1024]; // FIXME: handle ArrayOutOfBoundsException
		int nResults = TrieSearcher.commonPrefixSearch(getTrieBuffer(), copyOfIterator, resultArray);
		for (int i = 0; i < nResults; ++i)
			result.add(resultArray[i]);
		return result;
	}
}
