package jp.thisptr.util;

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
	
	private final int beginId;
	private int nextId;
	
	public SequencialIdMapper() {
		this(0);
	}
	
	public SequencialIdMapper(final int beginId) {
		this.beginId = beginId;
		this.nextId = beginId;
	}
	
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
			return objects.get(id - beginId);
		} finally {
			read.unlock();
		}
	}
	
	public Integer get(final T obj) {
		return objectId.get(obj);
	}
	
	private int mapIfAbsent(final T token) {
		write.lock();
		try {
			final Integer pid = objectId.putIfAbsent(token, nextId);
			if (pid == null) {
				objects.add(token);
				return nextId++;
			}
			return pid;
		} finally {
			write.unlock();
		}
	}
	
	public int map(final T obj) {
		final Integer id = get(obj);
		if (id != null)
			return id;
		return mapIfAbsent(obj);
	}
}