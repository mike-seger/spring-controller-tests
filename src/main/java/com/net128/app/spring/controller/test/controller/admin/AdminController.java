package com.net128.app.spring.controller.test.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.net128.shared.RestUtils.genericResponseBody;

@SuppressWarnings("unused")
@RestController
public class AdminController {
	@GetMapping("/admin")
	public ResponseEntity<Map<String, Object>> index() {
		return ResponseEntity.ok(genericResponseBody());
	}
}
