package net.thisptr.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import net.thisptr.lang.StopIteration;
import net.thisptr.lang.enumerator.SinglyEnumerator;

import org.apache.commons.io.IOUtils;

public final class IOEnumerators {
	private IOEnumerators() { }
	
	public static SinglyEnumerator<String> readLines(final File file, final Charset charset) throws FileNotFoundException {
		return readLines(new FileInputStream(file), charset);
	}
	
	public static SinglyEnumerator<String> readLines(final File file) throws FileNotFoundException {
		return readLines(new FileInputStream(file), Charset.defaultCharset());
	}
	
	public static SinglyEnumerator<String> readLines(final InputStream is, final Charset charset) {
		return readLines(new InputStreamReader(is, charset));
	}
	
	public static SinglyEnumerator<String> readLines(final InputStream is) {
		return readLines(new InputStreamReader(is, Charset.defaultCharset()));
	}
	
	public static SinglyEnumerator<String> readLines(final Reader reader) {
		final BufferedReader bufferedReader = reader instanceof BufferedReader
				? (BufferedReader) reader
				: new BufferedReader(reader);
				
		return new SinglyEnumerator<String>() {
			public String invoke() throws StopIteration {
				try {
					String line = bufferedReader.readLine();
					// FIXME: reader is not closed until completely read.
					if (line == null) {
						IOUtils.closeQuietly(bufferedReader);
						throw new StopIteration();
					}
					return line;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
}
