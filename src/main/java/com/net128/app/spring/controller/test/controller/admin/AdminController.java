package com.net128.app.spring.controller.test.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AdminController {
	@GetMapping("/admin")
	public String index(@RequestParam("context") String context) {
		return "Context: "+context;
	}
}
