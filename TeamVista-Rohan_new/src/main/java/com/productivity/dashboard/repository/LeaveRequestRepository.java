package com.productivity.dashboard.repository;

import com.productivity.dashboard.model.LeaveRequest;
import com.productivity.dashboard.model.LeaveStatus;
import com.productivity.dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for LeaveRequest entity operations
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    
    List<LeaveRequest> findByUserOrderByCreatedAtDesc(User user);
    
    List<LeaveRequest> findByUserAndStatusOrderByCreatedAtDesc(User user, LeaveStatus status);
    
    List<LeaveRequest> findByStatusOrderByCreatedAtDesc(LeaveStatus status);
    
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user.id IN :userIds ORDER BY lr.createdAt DESC")
    List<LeaveRequest> findByUserIds(@Param("userIds") List<Long> userIds);
    
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user.id IN :userIds AND lr.status = :status ORDER BY lr.createdAt DESC")
    List<LeaveRequest> findByUserIdsAndStatus(@Param("userIds") List<Long> userIds, @Param("status") LeaveStatus status);
    
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user = :user AND " +
           "((lr.startDate <= :endDate AND lr.endDate >= :startDate)) AND " +
           "lr.status IN ('PENDING', 'APPROVED')")
    List<LeaveRequest> findOverlappingLeaves(@Param("user") User user, 
                                             @Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(lr) FROM LeaveRequest lr WHERE lr.user = :user AND lr.status = :status")
    Long countByUserAndStatus(@Param("user") User user, @Param("status") LeaveStatus status);
    
    @Query("SELECT SUM(lr.daysCount) FROM LeaveRequest lr WHERE lr.user = :user AND lr.status = 'APPROVED' AND YEAR(lr.startDate) = :year")
    Long getTotalApprovedDaysByUserAndYear(@Param("user") User user, @Param("year") int year);
}