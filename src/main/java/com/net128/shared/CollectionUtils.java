package com.net128.shared;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CollectionUtils {
	public static <K, V> Map<K, V> sortedMapOf(K key, V value, List<Object> keyValues) {
		if(keyValues.size()%2==1) throw new IllegalArgumentException("Number of arguments must be even");
		var map = new LinkedHashMap<K, V>();
		map.put(key, value);
		for(int i = 0; i < keyValues.size(); i += 2)
			//noinspection unchecked
			map.put((K) keyValues.get(i), (V) keyValues.get(i + 1));
		return map;
	}
}
