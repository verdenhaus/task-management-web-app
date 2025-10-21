package com.example.taskmanagementwebapp.service;

import com.example.taskmanagementwebapp.dto.CreateUserRequest;
import com.example.taskmanagementwebapp.dto.UserDto;
import com.example.taskmanagementwebapp.entity.User;
import com.example.taskmanagementwebapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }
    public List<UserDto> getAllUsers() {
        return userRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public UserDto createUser(CreateUserRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User(req.getUsername(), req.getEmail(), req.getPassword(), "AVAILABLE");
        return toDto(userRepo.save(user));
    }

    public void deleteUser(Long id) { userRepo.deleteById(id); }

    private UserDto toDto(User u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setAvailabilityStatus(u.getAvailabilityStatus());
        return dto;
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto updatedUser, String currentUsername) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getEmail().equals(updatedUser.getEmail())
                && userRepo.existsByEmail(updatedUser.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());

        return toDto(userRepo.save(user));
    }

    public UserDto getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toDto(user);
    }

    public UserDto getUserByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toDto(user);
    }

    @Transactional
    public void changePasswordByIdentifier(String identifier, String currentPassword, String newPassword){
        User user = userRepo.findByUsername(identifier)
                .or(() -> userRepo.findByEmail(identifier))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getHashedPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setHashedPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }



}
