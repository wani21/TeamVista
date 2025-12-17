package com.productivity.dashboard.repository;

import com.productivity.dashboard.model.Group;
import com.productivity.dashboard.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByProject(Project project);
    
    Optional<Group> findByProjectId(Long projectId);
}
