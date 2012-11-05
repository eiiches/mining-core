package jp.thisptr.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import jp.thisptr.core.generator.SinglyGenerator;
import jp.thisptr.core.generator.signal.StopIteration;
import jp.thisptr.core.lambda.Lambda1;

public class IOUtils {
	private IOUtils() { }
	
	public static Lambda1<SinglyGenerator<String>, Reader> readLines() {
		return new ReadLines();
	}
	public static class ReadLines extends Lambda1<SinglyGenerator<String>, Reader> {
		public SinglyGenerator<String> invoke(final Reader reader) {
			final BufferedReader bufferedReader = new BufferedReader(reader);
			return new SinglyGenerator<String>() {
				public String invoke() throws StopIteration {
					try {
						String line = bufferedReader.readLine();
						if (line == null) {
							org.apache.commons.io.IOUtils.closeQuietly(bufferedReader);
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
}
