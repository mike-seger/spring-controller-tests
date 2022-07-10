package com.net128.app.spring.controller.test.controller.main;

import static com.net128.app.spring.controller.test.CollectionUtils.sortedMapOf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
@Slf4j
public class Controller {
	final static ObjectMapper om = new ObjectMapper();

	@GetMapping("/")
	@Operation(summary = "Get response for an offer")
	public ResponseEntity<Map<String, Object>> index(
		@RequestParam(value = "offer", defaultValue = "1000")
		@Schema(example = "1234")
		double offer,

		@RequestParam(value = "context", defaultValue = "{}")
		@Schema(description = "The context is a generic context object defined by the caller",
			example = "{\"attribute1\": 123, \"attribute2\": 456, \"attribute3\": \"ABCDEF\"}")
		String context,

		@RequestHeader(value = "context-attributes", defaultValue = "")
		@Schema(example = "attribute1, attribute2")
		String contextAttributes
	) {
		try {
			var typeRef = new TypeReference<LinkedHashMap<String, Object>>() {};
			var contextMap = om.readValue(context, typeRef);
			if(!StringUtils.isAllBlank(contextAttributes))  {
				var contextAttributeList = new ArrayList<>(List.of(
					contextAttributes.replaceAll("\\s", "").split(",")));
				contextAttributeList.removeAll(contextMap.keySet());
				if(contextAttributeList.size()>0) {
					return ResponseEntity.badRequest().body(sortedMapOf(
						"id", UUID.randomUUID(),
						"timestamp", LocalDateTime.now(),
						"message", "The following attributes are missing from the context: "+
							contextAttributeList.stream().map(String::valueOf)
								.collect(Collectors.joining(", "))
					));
				}
			}
			return ResponseEntity.ok(sortedMapOf(
				"id", UUID.randomUUID(),
				"timestamp", LocalDateTime.now(),
				"quote", 3.515*offer,
				"context", contextMap));
		} catch(Exception e) {
			throw new ValidationException("Failed to validate context", e);
		}
	}
}
