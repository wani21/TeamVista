package com.productivity.dashboard.controller;

import com.productivity.dashboard.dto.AddMemberRequest;
import com.productivity.dashboard.dto.ProjectCreateRequest;
import com.productivity.dashboard.dto.ProjectMemberDTO;
import com.productivity.dashboard.dto.ProjectUpdateRequest;
import com.productivity.dashboard.model.Project;
import com.productivity.dashboard.model.ProjectMemberRole;
import com.productivity.dashboard.model.Role;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.repository.UserRepository;
import com.productivity.dashboard.service.ProjectMemberService;
import com.productivity.dashboard.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final UserRepository userRepository;

    public ProjectController(ProjectService projectService, 
                             ProjectMemberService projectMemberService,
                             UserRepository userRepository) {
        this.projectService = projectService;
        this.projectMemberService = projectMemberService;
        this.userRepository = userRepository;
    }

    /**
     * Get projects - Managers see all, Employees see only their projects
     */
    @GetMapping
    public ResponseEntity<List<Project>> getProjects(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElse(null);
        
        if (user != null && user.getRole() == Role.MANAGER) {
            // Managers can see all projects
            return ResponseEntity.ok(projectService.getAllProjects());
        } else {
            // Employees only see projects they are members of
            return ResponseEntity.ok(projectService.getProjectsByUserEmail(userEmail));
        }
    }

    /**
     * Get project by ID - with membership check
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(projectService.getProjectByIdForUser(id, userEmail));
    }

    /**
     * Create a new project - MANAGERS ONLY
     * Auto assigns authenticated user as manager and adds specified members
     */
    @PostMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Project> createProject(
            @RequestBody ProjectCreateRequest request,
            Authentication authentication) {
        String managerEmail = authentication.getName();
        Project created = projectService.createProject(request, managerEmail);
        return ResponseEntity.ok(created);
    }

    /**
     * Update a project - MANAGERS ONLY
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectUpdateRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    /**
     * Delete a project - MANAGERS ONLY
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update project revenue/expenses - MANAGERS ONLY
     */
    @PatchMapping("/{id}/financials")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Project> updateProjectFinancials(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> financials) {
        BigDecimal revenue = financials.get("revenue");
        BigDecimal expenses = financials.get("expenses");
        return ResponseEntity.ok(projectService.updateProjectRevenue(id, revenue, expenses));
    }

    /**
     * Get all members of a project - accessible to project members
     */
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberDTO>> getProjectMembers(
            @PathVariable Long projectId,
            Authentication authentication) {
        // Verify user has access to the project
        String userEmail = authentication.getName();
        projectService.getProjectByIdForUser(projectId, userEmail);
        
        List<ProjectMemberDTO> members = projectMemberService.getProjectMembers(projectId);
        return ResponseEntity.ok(members);
    }

    /**
     * Add a member to a project - MANAGERS ONLY
     */
    @PostMapping("/{projectId}/members")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<ProjectMemberDTO> addMember(
            @PathVariable Long projectId,
            @RequestBody AddMemberRequest request) {
        ProjectMemberRole role = ProjectMemberRole.MEMBER;
        if (request.getRole() != null) {
            try {
                role = ProjectMemberRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                role = ProjectMemberRole.MEMBER;
            }
        }
        ProjectMemberDTO member = projectMemberService.addMember(projectId, request.getUserId(), role);
        return ResponseEntity.ok(member);
    }

    /**
     * Remove a member from a project - MANAGERS ONLY
     */
    @DeleteMapping("/{projectId}/members/{userId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        projectMemberService.removeMember(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if a user is a member of a project
     */
    @GetMapping("/{projectId}/members/{userId}/check")
    public ResponseEntity<Boolean> checkMembership(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(projectMemberService.isMember(projectId, userId));
    }
}
