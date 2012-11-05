package jp.thisptr.core.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import jp.thisptr.core.lambda.Lambda0;
import jp.thisptr.core.lambda.util.Lambdas;

public class DefaultMap<K, V> implements Map<K, V>, Serializable {
	
	private static final long serialVersionUID = -3659724972952089349L;
	
	private Map<K, V> map;
	private Lambda0<V> defaultValue;
	
	public DefaultMap(final Map<K, V> map, final V defaultValue) {
		this.map = map;
		this.defaultValue = Lambdas.<V>constant(defaultValue);
	}
	
	public DefaultMap(final Map<K, V> map, final Lambda0<V> defaultValue) {
		this.map = map;
		this.defaultValue = defaultValue;
	}
	
	public DefaultMap(final Map<K, V> map, final Class<V> defaultValue, final Object... args) {
		this.map = map;
		this.defaultValue = Lambdas.<V>constructor(defaultValue, args);
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
			value = defaultValue.invoke();
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
