package com.example.taskmanagementwebapp.controller;

import com.example.taskmanagementwebapp.dto.CreateTaskRequest;
import com.example.taskmanagementwebapp.dto.TaskDto;
import com.example.taskmanagementwebapp.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        TaskDto updated = taskService.updateStatus(id, newStatus);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long id,
            @RequestBody TaskDto updatedTaskDto
    ) {
        TaskDto updated = taskService.updateTask(id, updatedTaskDto);
        return ResponseEntity.ok(updated);
    }


}
