package jp.thisptr.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SequencialIdMapper<T> {
	private final ConcurrentHashMap<T, Integer> objectId = new ConcurrentHashMap<T, Integer>();
	private final List<T> objects = new ArrayList<T>();
	
	private final ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
	private final Lock read = rwlock.readLock();
	private final Lock write = rwlock.writeLock();
	
	public int size() {
		read.lock();
		try {
			return objects.size();
		} finally {
			read.unlock();
		}
	}
	
	public T reverse(final int id) {
		read.lock();
		try {
			return objects.get(id);
		} finally {
			read.unlock();
		}
	}
	
	public Integer get(final T word) {
		return objectId.get(word);
	}
	
	private int mapIfAbsent(final T token) {
		write.lock();
		try {
			Integer id = objectId.size();
			Integer pid = objectId.putIfAbsent(token, id);
			if (pid == null) {
				objects.add(token);
				return id;
			}
			return pid;
		} finally {
			write.unlock();
		}
	}
	
	public int map(final T obj) {
		Integer id = get(obj);
		if (id == null)
			id = mapIfAbsent(obj);
		return id;
	}
}