package com.example.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User service is temporarily unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "user-service");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "auth service is temporarily unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "auth-service");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> productServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product service is temporarily unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "product-service");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> orderServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order service is temporarily unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "order-service");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> generalFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Service is temporarily unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
