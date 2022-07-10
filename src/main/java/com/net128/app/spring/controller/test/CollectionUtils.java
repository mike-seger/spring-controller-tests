package com.net128.app.spring.controller.test;

import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionUtils {
	@SuppressWarnings("SameParameterValue")
	public static <K, V> Map<K, V> sortedMapOf(K key, V value, Object ... keyValues) {
		if(keyValues.length%2==1) throw new IllegalArgumentException("Number of arguments must be even");
		var map = new LinkedHashMap<K, V>();
		map.put(key, value);
		for(int i = 0; i < keyValues.length; i += 2)
			//noinspection unchecked
			map.put((K) keyValues[i], (V) keyValues[i+1]);
		return map;
	}
}
