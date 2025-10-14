package com.example.taskmanagementwebapp.repository;

import com.example.taskmanagementwebapp.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find all high-priority tasks
    @Query("SELECT t FROM Task t WHERE t.priorityLevel = 'HIGH'")
    List<Task> findHighPriorityTasks();

    // Find by assigned user
    List<Task> findByAssignedUserId(Long assignedUserId);

    // Find by priority (LOW, MEDIUM, HIGH)
    List<Task> findByPriorityLevelIgnoreCase(String priorityLevel);

    // Find tasks created after a certain date
    List<Task> findByCreationTimestampAfter(LocalDateTime date);

    // Find tasks with title or description containing keyword
    List<Task> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    // Pageable + filtering example
    Page<Task> findByPriorityLevel(String priorityLevel, Pageable pageable);

    // Reassign or clear a task’s user
    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.assignedUser.id = :userId WHERE t.id = :taskId")
    void assignUserToTask(Long taskId, Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.assignedUser = NULL WHERE t.id = :taskId")
    void unassignTask(Long taskId);
}
