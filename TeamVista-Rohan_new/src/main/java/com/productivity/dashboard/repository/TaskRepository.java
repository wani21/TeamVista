package com.productivity.dashboard.repository;

import com.productivity.dashboard.model.Task;
import com.productivity.dashboard.model.TaskStatus;
import com.productivity.dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByAssignedTo(User assignedTo);
    
    List<Task> findByStatus(TaskStatus status);
    
    List<Task> findByAssignedToAndStatus(User assignedTo, TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = 'COMPLETED'")
    long countCompletedTasks();
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = 'PENDING'")
    long countPendingTasks();
    
    @Query("SELECT COUNT(t) FROM Task t")
    long countTotalTasks();
    
    List<Task> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
    
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId")
    List<Task> findByAssignedToId(Long userId);
    
    @Query("SELECT t FROM Task t ORDER BY t.dueDate ASC")
    List<Task> findAllOrderByDueDateAsc();
    
    @Query("SELECT t FROM Task t ORDER BY t.priority DESC")
    List<Task> findAllOrderByPriorityDesc();
    
    @Query("SELECT t FROM Task t ORDER BY t.createdAt DESC")
    List<Task> findAllOrderByCreatedAtDesc();
}