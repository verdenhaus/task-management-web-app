package com.example.taskmanagementwebapp.controller;

import com.example.taskmanagementwebapp.entity.User;
import com.example.taskmanagementwebapp.repository.UserRepository;
import com.example.taskmanagementwebapp.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder;

    public AuthController(UserRepository userRepo, AuthenticationManager authManager, JwtService jwtService, BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> req) {
        if (userRepo.existsByEmail(req.get("email"))) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        User user = new User(req.get("username"), req.get("email"), encoder.encode(req.get("password")), "AVAILABLE");
        userRepo.save(user);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.get("email"), req.get("password")));
        String token = jwtService.generateToken(req.get("email"));
        return ResponseEntity.ok(Map.of("token", token));
    }
}
