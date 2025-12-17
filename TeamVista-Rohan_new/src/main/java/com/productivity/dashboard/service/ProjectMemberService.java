package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.ProjectMemberDTO;
import com.productivity.dashboard.exception.BadRequestException;
import com.productivity.dashboard.exception.NotFoundException;
import com.productivity.dashboard.model.*;
import com.productivity.dashboard.repository.ProjectMemberRepository;
import com.productivity.dashboard.repository.ProjectRepository;
import com.productivity.dashboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectMemberService(ProjectMemberRepository projectMemberRepository,
                                ProjectRepository projectRepository,
                                UserRepository userRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<ProjectMemberDTO> getProjectMembers(Long projectId) {
        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);
        return members.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<User> getUsersByProjectId(Long projectId) {
        return projectMemberRepository.findUsersByProjectId(projectId);
    }

    @Transactional
    public ProjectMemberDTO addMember(Long projectId, Long userId, ProjectMemberRole role) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        // Check if user is already a member
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new BadRequestException("User is already a member of this project");
        }

        ProjectMember member = new ProjectMember(project, user, role != null ? role : ProjectMemberRole.MEMBER);
        ProjectMember saved = projectMemberRepository.save(member);
        return convertToDTO(saved);
    }

    @Transactional
    public void removeMember(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new NotFoundException("Member not found in project"));
        projectMemberRepository.delete(member);
    }

    public boolean isMember(Long projectId, Long userId) {
        return projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    private ProjectMemberDTO convertToDTO(ProjectMember member) {
        ProjectMemberDTO dto = new ProjectMemberDTO();
        dto.setId(member.getId());
        dto.setProjectId(member.getProject().getId());
        dto.setUserId(member.getUser().getId());
        dto.setUserName(member.getUser().getName());
        dto.setUserEmail(member.getUser().getEmail());
        dto.setRole(member.getRole().name());
        dto.setJoinedAt(member.getJoinedAt());
        return dto;
    }
}

