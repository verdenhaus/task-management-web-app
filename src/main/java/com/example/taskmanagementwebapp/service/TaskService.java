package com.example.taskmanagementwebapp.service;

import com.example.taskmanagementwebapp.dto.CreateTaskRequest;
import com.example.taskmanagementwebapp.dto.TaskDto;
import com.example.taskmanagementwebapp.entity.Task;
import com.example.taskmanagementwebapp.entity.User;
import com.example.taskmanagementwebapp.repository.TaskRepository;
import com.example.taskmanagementwebapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepo;
    private final UserRepository userRepo;

    public TaskService(TaskRepository taskRepo, UserRepository userRepo) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }

    public List<TaskDto> getAll(String priority, Long assigneeId) {
        List<Task> tasks;
        if (priority != null) tasks = taskRepo.findByPriorityLevelIgnoreCase(priority);
        else if (assigneeId != null) tasks = taskRepo.findByAssignedUserId(assigneeId);
        else tasks = taskRepo.findAll();
        return tasks.stream().map(this::toDto).collect(Collectors.toList());
    }

    public TaskDto create(CreateTaskRequest req) {
        Task t = new Task(req.getTitle(), req.getDescription(), req.getPriorityLevel());
        if (req.getAssignedUserId() != null) {
            userRepo.findById(req.getAssignedUserId()).ifPresent(t::setAssignedUser);
        }
        return toDto(taskRepo.save(t));
    }

    public void delete(Long id) { taskRepo.deleteById(id); }

    public TaskDto assignAuto(Long taskId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (task.getAssignedUser() != null)
            throw new IllegalStateException("Task already assigned");

        List<User> available = userRepo.findAvailableUsers();
        if (available.isEmpty())
            throw new IllegalStateException("No available users");

        User chosen = available.get(0);
        task.setAssignedUser(chosen);
        chosen.setAvailabilityStatus("BUSY");
        userRepo.save(chosen);
        return toDto(taskRepo.save(task));
    }

    private TaskDto toDto(Task t) {
        TaskDto dto = new TaskDto();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setPriorityLevel(t.getPriorityLevel());
        dto.setCreationTimestamp(t.getCreationTimestamp());
        if (t.getAssignedUser() != null)
            dto.setAssignedUserId(t.getAssignedUser().getId());
        return dto;
    }
}
