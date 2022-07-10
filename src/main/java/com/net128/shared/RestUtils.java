package com.net128.shared;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.net128.shared.CollectionUtils.sortedMapOf;

public class RestUtils {
	public static Map<String, Object> genericResponseBody(Object ...keyValues) {
		var keyValueList = new ArrayList<Object>(List.of("timestamp", LocalDateTime.now()));
		keyValueList.addAll(List.of(keyValues));
		return sortedMapOf("id", UUID.randomUUID(), keyValueList);
	}
}
