package jp.thisptr.core.util;

import java.util.Map;
import java.util.Map.Entry;

import jp.thisptr.core.lambda.Lambda1;
import jp.thisptr.core.lambda.Lambda2;

public final class MapUtils {
	private MapUtils() { }
	
	public static <K, V> Lambda1<K, Map.Entry<K, V>> getKey() {
		return new GetKey<K, V>();
	}
	public static class GetKey<K, V> extends Lambda1<K, Map.Entry<K, V>> {
		public K invoke(final Entry<K, V> arg1) {
			return arg1.getKey();
		}
	}
	public static <K, V> Lambda1<V, Map.Entry<K, V>> getValue() {
		return new GetValue<K, V>();
	}
	public static class GetValue<K, V> extends Lambda1<V, Map.Entry<K, V>> {
		public V invoke(final Entry<K, V> arg1) {
			return arg1.getValue();
		}
	}
	
	public static <K, V> Lambda2<V, Map<K, V>, K> getMapValue() {
		return new GetMapValue<K, V>();
	}
	public static <K, V> Lambda1<V, K> getMapValue(final Map<K, V> map) {
		return new GetMapValue<K, V>().bind(map);
	}
	public static class GetMapValue<K, V> extends Lambda2<V, Map<K, V>, K> {
		public V invoke(final Map<K, V> map, final K key) {
			return map.get(key);
		}
	}
}
