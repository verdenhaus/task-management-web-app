package com.example.taskmanagementwebapp.controller;

import com.example.taskmanagementwebapp.dto.CreateTaskRequest;
import com.example.taskmanagementwebapp.dto.TaskDto;
import com.example.taskmanagementwebapp.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) { this.taskService = taskService; }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAll(
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long assigneeId) {
        return ResponseEntity.ok(taskService.getAll(priority, assigneeId));
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@RequestBody CreateTaskRequest req) {
        return ResponseEntity.ok(taskService.create(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign/{id}")
    public ResponseEntity<TaskDto> assign(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.assignAuto(id));
    }
}
