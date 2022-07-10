package com.net128.app.spring.controller.test.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.net128.app.spring.controller.test.CollectionUtils.sortedMapOf;

@RestController
public class AdminController {
	@GetMapping("/admin")
	public ResponseEntity<Map<String, Object>> index() {
		return ResponseEntity.ok(sortedMapOf(
			"id", UUID.randomUUID(),
			"timestamp", LocalDateTime.now()));
	}
}
