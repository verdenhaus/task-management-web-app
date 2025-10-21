package com.example.taskmanagementwebapp.service;

import com.example.taskmanagementwebapp.dto.CreateTaskRequest;
import com.example.taskmanagementwebapp.dto.TaskDto;
import com.example.taskmanagementwebapp.entity.Task;
import com.example.taskmanagementwebapp.entity.User;
import com.example.taskmanagementwebapp.repository.TaskRepository;
import com.example.taskmanagementwebapp.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        t.setStatus(Task.Status.ASSIGNED);
        if (req.getAssignedUserId() != null) {
            userRepo.findById(req.getAssignedUserId()).ifPresent(t::setAssignedUser);
        }
        return toDto(taskRepo.save(t));
    }

    public void delete(Long id) {
        taskRepo.deleteById(id);
    }

    @Transactional
    public TaskDto assignAuto(Long taskId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (task.getAssignedUser() != null)
            throw new IllegalStateException("Task already assigned");

        List<User> candidates = userRepo.findAvailableForAutoAssign();
        if (candidates.isEmpty())
            throw new IllegalStateException("All users are currently busy. Try again later or assign manually.");

        User chosen = userRepo.findById(candidates.get(0).getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        task.setAssignedUser(chosen);


        long activeCount = taskRepo.countByAssignedUserIdAndStatusNot(chosen.getId(), Task.Status.COMPLETE);
        chosen.setAvailabilityStatus(activeCount + 1 >= 3 ? "BUSY" : "AVAILABLE");

        System.out.println("Chosen user: " + chosen.getId() + ", assigned to task: " + task.getId());
        userRepo.save(chosen);

        Task saved = taskRepo.save(task);
        System.out.println("After save, assignedUser = " + (saved.getAssignedUser() != null ? saved.getAssignedUser().getId() : null));

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
        dto.setStatus(t.getStatus().name());
        return dto;
    }

    @Transactional
    public TaskDto updateStatus(Long taskId, String newStatus) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        try {
            Task.Status statusEnum = Task.Status.valueOf(newStatus.toUpperCase());
            task.setStatus(statusEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }
        if (task.getStatus() == Task.Status.COMPLETE && task.getAssignedUser() != null) {
            User user = task.getAssignedUser();
            long activeCount = taskRepo.countByAssignedUserIdAndStatusNot(user.getId(), Task.Status.COMPLETE);
            user.setAvailabilityStatus(activeCount < 3 ? "AVAILABLE" : "BUSY");
            userRepo.save(user);
        }

        return toDto(taskRepo.save(task));
    }

    @Transactional
    public TaskDto updateTask(Long id, TaskDto updatedTaskDto) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setTitle(updatedTaskDto.getTitle());
        task.setDescription(updatedTaskDto.getDescription());
        task.setPriorityLevel(updatedTaskDto.getPriorityLevel().toUpperCase());

        if (updatedTaskDto.getAssignedUserId() != null) {
            User user = userRepo.findById(updatedTaskDto.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            task.setAssignedUser(user);
        } else {
            task.setAssignedUser(null);
        }

        return toDto(taskRepo.save(task));
    }

}
