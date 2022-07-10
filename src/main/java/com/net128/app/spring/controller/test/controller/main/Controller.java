package com.net128.app.spring.controller.test.controller.main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
@Slf4j
public class Controller {
	final static ObjectMapper om = new ObjectMapper();

	@GetMapping("/")
	public ResponseEntity<String> index(
		@RequestParam(value = "context", defaultValue = "{}") String context,
		@RequestHeader(value = "context-attributes", defaultValue = "") String contextAttributes
	) {
		try {
			var typeRef = new TypeReference<HashMap<String, String>>() {};
			var contextMap = om.readValue(context, typeRef);
			if(!StringUtils.isAllBlank(contextAttributes))  {
				var contextAttributeList = new ArrayList<>(List.of(
					contextAttributes.replaceAll("\\s", "").split(",")));
				contextAttributeList.removeAll(contextMap.keySet());
				if(contextAttributeList.size()>0) {
					return ResponseEntity.badRequest().body(
						"The following attributes are missing from the context: "+
						contextAttributeList.stream().map(String::valueOf)
							.collect(Collectors.joining(", "))
					);
				}
			}
			return ResponseEntity.ok("Context: " + contextMap);
		} catch(Exception e) {
			throw new ValidationException("Failed to validate context", e);
		}
	}

	@GetMapping("/index2")
	public String index2(@RequestParam Map<String,String> input) {
		return "Input: " + input;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ValidationException.class)
	public String handleValidationExceptions(ValidationException ex) {
		log.warn("", ex);
		return ex.getMessage();
	}
}
