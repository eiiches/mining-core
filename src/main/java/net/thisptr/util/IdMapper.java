package net.thisptr.util;

import java.util.Set;

public interface IdMapper<T> {
	int size();
	T reverse(int id);
	int get(T obj);
	int map(T obj);
	Set<T> keySet();
}