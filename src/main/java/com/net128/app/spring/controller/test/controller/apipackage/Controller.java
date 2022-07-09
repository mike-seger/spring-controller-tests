package com.net128.app.spring.controller.test.controller.apipackage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class Controller {
	final static ObjectMapper om = new ObjectMapper();

	@GetMapping("/")
	public String index(@RequestParam(value = "context", required = false) String context) {
		try {
			var typeRef = new TypeReference<HashMap<String, String>>() {};
			return "Context: " + om.readValue(context, typeRef);
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
