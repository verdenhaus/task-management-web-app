package com.example.taskmanagementwebapp.repository;

import com.example.taskmanagementwebapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find by email for login/registration
    Optional<User> findByEmail(String email);

    // Check if a user with this email already exists
    boolean existsByEmail(String email);

    // Find users by partial username for search
    List<User> findByUsernameContainingIgnoreCase(String keyword);

    // Find available users
    @Query("SELECT u FROM User u WHERE u.availabilityStatus = 'AVAILABLE'")
    List<User> findAvailableUsers();

    // Paginated list of users
    Page<User> findAll(Pageable pageable);

    // Update availability (custom)
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.availabilityStatus = :status WHERE u.id = :userId")
    void updateAvailability(Long userId, String status);
}
