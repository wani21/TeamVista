package com.productivity.dashboard.repository;

import com.productivity.dashboard.model.Attendance;
import com.productivity.dashboard.model.AttendanceStatus;
import com.productivity.dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Attendance entity operations
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    Optional<Attendance> findByUserAndDate(User user, LocalDate date);
    
    List<Attendance> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.user.id IN :userIds AND a.date BETWEEN :startDate AND :endDate ORDER BY a.date DESC")
    List<Attendance> findByUserIdsAndDateRange(@Param("userIds") List<Long> userIds, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user = :user AND a.status = :status AND a.date BETWEEN :startDate AND :endDate")
    Long countByUserAndStatusAndDateRange(@Param("user") User user, @Param("status") AttendanceStatus status, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.date = :date ORDER BY a.user.name")
    List<Attendance> findByDate(@Param("date") LocalDate date);
    
    @Query("SELECT AVG(a.workHours) FROM Attendance a WHERE a.user = :user AND a.workHours IS NOT NULL AND a.date BETWEEN :startDate AND :endDate")
    Double getAverageWorkHoursByUser(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}