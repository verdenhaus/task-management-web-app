package com.example.taskmanagementwebapp;

import com.example.taskmanagementwebapp.repository.TaskRepository;
import com.example.taskmanagementwebapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest { //Cleanup test data

    @Autowired protected MockMvc mockMvc;
    @Autowired protected UserRepository userRepo;
    @Autowired protected TaskRepository taskRepo;

    @BeforeEach
    @Transactional
    void cleanTestData() {
        var testTasks = taskRepo.findAll().stream()
                .filter(t -> t.getTitle() != null && t.getTitle().startsWith("__test__"))
                .collect(Collectors.toList());
        taskRepo.deleteAll(testTasks);

        var testUsers = userRepo.findAll().stream()
                .filter(u -> u.getUsername() != null && u.getUsername().startsWith("__test__"))
                .collect(Collectors.toList());
        userRepo.deleteAll(testUsers);
    }
}
