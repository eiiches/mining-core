package jp.thisptr.lang.collection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TypeMap<BaseType> {
	private final Map<Class<? extends BaseType>, BaseType> map;
	
	public TypeMap() {
		map = new HashMap<>();
	}
	
	public TypeMap(final int initialCapacity) {
		map = new HashMap<>(initialCapacity);
	}
	
	public TypeMap(final int initialCapacity, final float loadFactor) {
		map = new HashMap<>(initialCapacity, loadFactor);
	}
	
	public TypeMap(final TypeMap<BaseType> src) {
		map = new HashMap<>(src.size());
		for (final Map.Entry<Class<? extends BaseType>, BaseType> entry : src.entrySet())
			map.put(entry.getKey(), entry.getValue());
	}
	
	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(final Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(final Object value) {
		return map.containsValue(value);
	}

	public <T extends BaseType> T get(final Class<T> key) {
		return key.cast(map.get(key));
	}

	public <T extends BaseType> T put(final Class<? extends T> key, final T value) {
		return key.cast(map.put(key, value));
	}

	public <T extends BaseType> T remove(final Class<T> key) {
		return key.cast(map.remove(key));
	}

	public void putAll(final Map<? extends Class<? extends BaseType>, ? extends BaseType> m) {
		map.putAll(m);
	}

	public void clear() {
		map.clear();
	}

	public Set<Class<? extends BaseType>> keySet() {
		return map.keySet();
	}

	public Collection<BaseType> values() {
		return map.values();
	}

	public Set<Map.Entry<Class<? extends BaseType>, BaseType>> entrySet() {
		return map.entrySet();
	}

	@Override
	public String toString() {
		return "TypeMap [map=" + map + "]";
	}
}
