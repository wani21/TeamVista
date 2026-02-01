package com.productivity.dashboard.repository;

import com.productivity.dashboard.model.TimeEntry;
import com.productivity.dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TimeEntry entity operations
 */
@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    
    List<TimeEntry> findByUserOrderByStartTimeDesc(User user);
    
    @Query("SELECT t FROM TimeEntry t WHERE t.user = :user AND t.endTime IS NULL")
    Optional<TimeEntry> findRunningTimerByUser(@Param("user") User user);
    
    @Query("SELECT t FROM TimeEntry t WHERE t.user = :user AND t.startTime >= :start AND t.startTime <= :end ORDER BY t.startTime DESC")
    List<TimeEntry> findByUserAndDateRange(@Param("user") User user, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(t.durationMinutes) FROM TimeEntry t WHERE t.user = :user AND t.startTime >= :start AND t.startTime <= :end")
    Long getTotalMinutesByUserAndDateRange(@Param("user") User user, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT t FROM TimeEntry t WHERE t.user.id IN :userIds AND t.startTime >= :start AND t.startTime <= :end ORDER BY t.startTime DESC")
    List<TimeEntry> findByUserIdsAndDateRange(@Param("userIds") List<Long> userIds, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT t FROM TimeEntry t WHERE t.task.id = :taskId ORDER BY t.startTime DESC")
    List<TimeEntry> findByTaskId(@Param("taskId") Long taskId);
}