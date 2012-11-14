package jp.thisptr.core.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class DefaultMap<K, V> implements Map<K, V>, Serializable {
	private static final long serialVersionUID = -3659724972952089349L;
	
	private Map<K, V> map;
	
	public abstract V defaultValue();
	
	public DefaultMap(final Map<K, V> map) {
		this.map = map;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(final Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return map.containsValue(value);
	}
	
	public V find(final K key) {
		return map.get(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public V get(final Object key) {
		V value = map.get(key);
		if (value == null) {
			value = defaultValue();
			map.put((K) key, value);
		}
		return value;
	}

	@Override
	public V put(final K key, final V value) {
		return map.put(key, value);
	}

	@Override
	public V remove(final Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

}
