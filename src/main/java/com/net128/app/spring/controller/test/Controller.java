package com.net128.app.spring.controller.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class Controller {
	final static ObjectMapper om = new ObjectMapper();

	@GetMapping("/")
	public String index(@RequestParam("context")  String context) throws JsonProcessingException {
		TypeReference<HashMap<String,String>> typeRef = new TypeReference<>() {};
		return "Context: " + om.readValue(context, typeRef);
	}
}
