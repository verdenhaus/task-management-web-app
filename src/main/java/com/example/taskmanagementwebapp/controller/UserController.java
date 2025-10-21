package com.example.taskmanagementwebapp.controller;

import com.example.taskmanagementwebapp.dto.CreateUserRequest;
import com.example.taskmanagementwebapp.dto.PasswordChangeRequest;
import com.example.taskmanagementwebapp.dto.UserDto;
import com.example.taskmanagementwebapp.entity.User;
import com.example.taskmanagementwebapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername(), user.getEmail()));
    }



    @GetMapping
    public ResponseEntity<List<UserDto>> all() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateUserRequest req) {
        return ResponseEntity.ok(userService.createUser(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto updatedUser,
            Principal principal) {

        try {
            UserDto user = userService.updateUser(id, updatedUser, principal.getName());
            return ResponseEntity.ok(user);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Failed to update profile"));
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> changeMyPassword(
            @RequestBody PasswordChangeRequest req,
            Authentication authentication) {

        String username = authentication.getName();
        System.out.println("Authenticated username = " + username);
        userService.changePasswordByIdentifier(username, req.getCurrentPassword(), req.getNewPassword());
        return ResponseEntity.ok().build();
    }






}
