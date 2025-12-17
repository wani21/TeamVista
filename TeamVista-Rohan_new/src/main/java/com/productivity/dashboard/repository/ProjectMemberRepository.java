package com.productivity.dashboard.repository;

import com.productivity.dashboard.model.Project;
import com.productivity.dashboard.model.ProjectMember;
import com.productivity.dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    
    List<ProjectMember> findByProject(Project project);
    
    List<ProjectMember> findByProjectId(Long projectId);
    
    List<ProjectMember> findByUser(User user);
    
    List<ProjectMember> findByUserId(Long userId);
    
    Optional<ProjectMember> findByProjectAndUser(Project project, User user);
    
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);
    
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
    
    @Modifying
    @Query("DELETE FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.user.id = :userId")
    void deleteByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);
    
    @Query("SELECT pm.user FROM ProjectMember pm WHERE pm.project.id = :projectId")
    List<User> findUsersByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT pm.project FROM ProjectMember pm WHERE pm.user.id = :userId")
    List<Project> findProjectsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT pm.project FROM ProjectMember pm WHERE pm.user.email = :email")
    List<Project> findProjectsByUserEmail(@Param("email") String email);
}

