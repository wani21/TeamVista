package com.productivity.dashboard.repository;

import com.productivity.dashboard.model.Activity;
import com.productivity.dashboard.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Activity entity operations
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    Page<Activity> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    @Query("SELECT a FROM Activity a WHERE a.user.id IN :userIds ORDER BY a.createdAt DESC")
    Page<Activity> findTeamActivities(@Param("userIds") List<Long> userIds, Pageable pageable);
    
    @Query("SELECT a FROM Activity a ORDER BY a.createdAt DESC")
    Page<Activity> findAllActivities(Pageable pageable);
    
    List<Activity> findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(
        User user, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.user = :user AND a.createdAt >= :since")
    Long countUserActivitiesSince(@Param("user") User user, @Param("since") LocalDateTime since);
    
    @Query("SELECT a FROM Activity a WHERE a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<Activity> findRecentActivities(@Param("since") LocalDateTime since, Pageable pageable);
}