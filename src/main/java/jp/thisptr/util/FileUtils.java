package jp.thisptr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import jp.thisptr.lang.generator.SinglyGenerator;
import jp.thisptr.lang.lambda.alias.Lambda;

public final class FileUtils {
	private FileUtils() { }
	
	public static Lambda<SinglyGenerator<String>, File> readLines() {
		return new ReadLines();
	}
	public static Lambda<SinglyGenerator<String>, File> readLines(final Charset charset) {
		return new ReadLines(charset);
	}
	
	public static class ReadLines extends Lambda<SinglyGenerator<String>, File> {
		private Charset charset;
		public ReadLines() {
			this(Charset.defaultCharset());
		}
		public ReadLines(final Charset charset) {
			this.charset = charset;
		}
		public SinglyGenerator<String> invoke(final File file) {
			try {
				final Reader reader = new InputStreamReader(new FileInputStream(file), charset);
				return new IOUtils.ReadLines().invoke(reader);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
