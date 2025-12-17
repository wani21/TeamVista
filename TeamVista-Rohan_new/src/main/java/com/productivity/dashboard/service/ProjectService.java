package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.ProjectCreateRequest;
import com.productivity.dashboard.dto.ProjectUpdateRequest;
import com.productivity.dashboard.exception.ForbiddenException;
import com.productivity.dashboard.exception.NotFoundException;
import com.productivity.dashboard.model.*;
import com.productivity.dashboard.repository.GroupRepository;
import com.productivity.dashboard.repository.ProjectMemberRepository;
import com.productivity.dashboard.repository.ProjectRepository;
import com.productivity.dashboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final GroupRepository groupRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, 
                          GroupRepository groupRepository,
                          ProjectMemberRepository projectMemberRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.groupRepository = groupRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    // Get all projects (for managers only)
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
    
    // Get projects for a specific user (only projects they are member of)
    public List<Project> getProjectsByUserEmail(String email) {
        return projectMemberRepository.findProjectsByUserEmail(email);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project not found: " + id));
    }
    
    // Get project by ID with membership check
    public Project getProjectByIdForUser(Long id, String userEmail) {
        Project project = getProjectById(id);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        // Check if user is a member or manager
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(id, user.getId());
        boolean isProjectManager = project.getManager() != null && 
                project.getManager().getEmail().equals(userEmail);
        boolean isSystemManager = user.getRole() == Role.MANAGER;
        
        if (!isMember && !isProjectManager && !isSystemManager) {
            throw new ForbiddenException("You don't have access to this project");
        }
        
        return project;
    }

    @Transactional
    public Project createProject(ProjectCreateRequest request, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new NotFoundException("User not found: " + managerEmail));

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setManager(manager);
        
        if (request.getStatus() != null) {
            try {
                project.setStatus(ProjectStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                project.setStatus(ProjectStatus.PLANNING);
            }
        }
        
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setBudget(request.getBudget() != null ? request.getBudget() : BigDecimal.ZERO);
        project.setRevenue(request.getRevenue() != null ? request.getRevenue() : BigDecimal.ZERO);
        project.setExpenses(request.getExpenses() != null ? request.getExpenses() : BigDecimal.ZERO);

        Project saved = projectRepository.save(project);

        // Auto-create project group
        Group group = new Group();
        group.setName(saved.getName() + " Group");
        group.setType(GroupType.PROJECT_TEAM);
        group.setProject(saved);
        groupRepository.save(group);

        // Auto-add manager as project member (OWNER)
        ProjectMember managerMember = new ProjectMember(saved, manager, ProjectMemberRole.OWNER);
        projectMemberRepository.save(managerMember);

        // Add additional members if provided
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            for (Long memberId : request.getMemberIds()) {
                // Skip if it's the manager (already added)
                if (memberId.equals(manager.getId())) {
                    continue;
                }
                
                userRepository.findById(memberId).ifPresent(user -> {
                    ProjectMember member = new ProjectMember(saved, user, ProjectMemberRole.MEMBER);
                    projectMemberRepository.save(member);
                });
            }
        }

        return saved;
    }

    @Transactional
    public Project updateProject(Long id, ProjectUpdateRequest request) {
        Project project = getProjectById(id);
        
        if (request.getName() != null) {
            project.setName(request.getName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            try {
                project.setStatus(ProjectStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                // Keep current status
            }
        }
        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }
        if (request.getBudget() != null) {
            project.setBudget(request.getBudget());
        }
        if (request.getRevenue() != null) {
            project.setRevenue(request.getRevenue());
        }
        if (request.getExpenses() != null) {
            project.setExpenses(request.getExpenses());
        }

        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }

    @Transactional
    public Project updateProjectRevenue(Long id, BigDecimal revenue, BigDecimal expenses) {
        Project project = getProjectById(id);
        
        if (revenue != null) {
            project.setRevenue(revenue);
        }
        if (expenses != null) {
            project.setExpenses(expenses);
        }
        
        return projectRepository.save(project);
    }
}
