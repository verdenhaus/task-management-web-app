package com.example.taskmanagementwebapp.config;

import com.example.taskmanagementwebapp.entity.Task;
import com.example.taskmanagementwebapp.entity.User;
import com.example.taskmanagementwebapp.repository.TaskRepository;
import com.example.taskmanagementwebapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final TaskRepository taskRepo;

    public DataInitializer(UserRepository userRepo, TaskRepository taskRepo) {
        this.userRepo = userRepo;
        this.taskRepo = taskRepo;
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() == 0) {
            User u1 = new User("Alice", "alice@mail.ru", "12345678", "AVAILABLE");
            User u2 = new User("Mia", "mia@mail.ru", "12345678", "AVAILABLE");
            User u3 = new User("Luna", "luna@mail.ru", "12345678", "BUSY");
            userRepo.saveAll(List.of(u1, u2, u3));

            Task t1 = new Task("Fix login bug", "Resolve login redirect issue", "HIGH");
            Task t2 = new Task("Write API docs", "Document all endpoints", "MEDIUM");
            Task t3 = new Task("Add unit tests", "Increase coverage to 80%", "HIGH");
            Task t4 = new Task("Refactor code", "Clean unused imports", "LOW");
            Task t5 = new Task("Prepare demo", "Set up Postman collection", "MEDIUM");

            // assign a few tasks
            t1.setAssignedUser(u1);
            t2.setAssignedUser(u1);
            t3.setAssignedUser(u2);
            t4.setAssignedUser(u3);
            t5.setAssignedUser(u2);

            taskRepo.saveAll(List.of(t1, t2, t3, t4, t5));
        }
    }
}
