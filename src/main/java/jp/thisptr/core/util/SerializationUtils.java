package jp.thisptr.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SerializationUtils {
	private SerializationUtils() { }
	
	public static <T extends Serializable> void saveObject(final T obj, final File file) throws IOException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(obj);
		}
	}
	public static <T extends Serializable> T loadObject(final File file) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			@SuppressWarnings("unchecked")
			T result = (T) ois.readObject();
			return result;
		}
	}
}
