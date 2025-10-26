package com.productivity.dashboard.repository;

import com.productivity.dashboard.model.Task;
import com.productivity.dashboard.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Task entity operations
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find tasks assigned to a specific user
     * @param assignedToId the user ID
     * @return list of tasks assigned to the user
     */
    List<Task> findByAssignedToId(Long assignedToId);

    /**
     * Find tasks by status
     * @param status the task status
     * @return list of tasks with the specified status
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Find tasks by assigned user and status
     * @param assignedToId the user ID
     * @param status the task status
     * @return list of tasks matching both criteria
     */
    List<Task> findByAssignedToIdAndStatus(Long assignedToId, TaskStatus status);

    /**
     * Count total tasks
     * @return total number of tasks
     */
    @Query("SELECT COUNT(t) FROM Task t")
    long countTotalTasks();

    /**
     * Count completed tasks
     * @return number of completed tasks
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = 'COMPLETED'")
    long countCompletedTasks();

    /**
     * Count pending tasks (PENDING + IN_PROGRESS)
     * @return number of pending tasks
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status IN ('PENDING', 'IN_PROGRESS')")
    long countPendingTasks();

    /**
     * Find completed tasks that were finished on or before their due date
     * @return list of on-time completed tasks
     */
    @Query("SELECT t FROM Task t WHERE t.status = 'COMPLETED' AND t.completedDate <= t.dueDate")
    List<Task> findOnTimeCompletedTasks();

    /**
     * Count tasks assigned to a specific user
     * @param userId the user ID
     * @return number of tasks assigned to the user
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.id = :userId")
    long countTasksByUserId(@Param("userId") Long userId);

    /**
     * Count completed tasks for a specific user
     * @param userId the user ID
     * @return number of completed tasks for the user
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.id = :userId AND t.status = 'COMPLETED'")
    long countCompletedTasksByUserId(@Param("userId") Long userId);
}