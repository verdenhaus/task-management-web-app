package com.example.taskmanagementwebapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(1)) {
                return ResponseEntity.ok("UP");
            } else {
                return ResponseEntity.status(503).body("DOWN");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(503).body("DB DOWN: " + e.getMessage());
        }
    }
}
