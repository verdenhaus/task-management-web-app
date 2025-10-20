package com.example.taskmanagementwebapp.service;

import com.example.taskmanagementwebapp.dto.CreateUserRequest;
import com.example.taskmanagementwebapp.dto.UserDto;
import com.example.taskmanagementwebapp.entity.User;
import com.example.taskmanagementwebapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) { this.userRepo = userRepo; }

    public List<UserDto> getAllUsers() {
        return userRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
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
}
