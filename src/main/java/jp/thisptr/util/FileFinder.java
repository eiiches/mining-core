package jp.thisptr.util;

import java.io.File;

import jp.thisptr.lang.generator.Generators;
import jp.thisptr.lang.generator.SinglyGenerator;
import jp.thisptr.lang.lambda.alias.Predicate;

public class FileFinder {
	public enum Type {
		File,
		Directory
	}
	
	private String basePath;
	private String name;
	private Type type;
	
	public FileFinder() { this("."); }
	public FileFinder(final String basePath) {
		this.basePath = basePath;
	}
	
	public FileFinder name(final String regex) {
		this.name = regex;
		return this;
	}
	
	public FileFinder type(final Type type) {
		this.type = type;
		return this;
	}
	
	public SinglyGenerator<File> find() {
		File base = new File(basePath);
		SinglyGenerator<File> result = Generators.array(base.listFiles());
		if (name != null)
			result = result.filter(new Predicate<File>() { public Boolean invoke(final File f) {
				return f.getName().matches(name);
			}});
		if (type != null)
			result = result.filter(new Predicate<File>() { public Boolean invoke(final File f) {
				switch (type) {
				case File:
					return f.isFile();
				case Directory:
					return f.isDirectory();
				default:
					return false;
				}
			}});
		return result;
	}
}
