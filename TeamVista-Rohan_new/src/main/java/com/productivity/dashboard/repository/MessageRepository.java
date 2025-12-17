package com.productivity.dashboard.repository;

import com.productivity.dashboard.model.Group;
import com.productivity.dashboard.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByGroupOrderByCreatedAtAsc(Group group);
    
    List<Message> findByGroupIdOrderByCreatedAtAsc(Long groupId);
    
    @Query("SELECT m FROM Message m WHERE m.group.id = :groupId ORDER BY m.createdAt DESC")
    List<Message> findRecentMessagesByGroupId(@Param("groupId") Long groupId);
}

