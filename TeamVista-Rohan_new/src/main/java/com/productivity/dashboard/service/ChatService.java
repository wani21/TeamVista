package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.MessageDTO;
import com.productivity.dashboard.exception.ForbiddenException;
import com.productivity.dashboard.exception.NotFoundException;
import com.productivity.dashboard.model.*;
import com.productivity.dashboard.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final MessageRepository messageRepository;
    private final GroupRepository groupRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public ChatService(MessageRepository messageRepository, 
                       GroupRepository groupRepository,
                       ProjectMemberRepository projectMemberRepository,
                       UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.groupRepository = groupRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    public Group getGroupByProjectId(Long projectId) {
        return groupRepository.findByProjectId(projectId)
                .orElseThrow(() -> new NotFoundException("Group not found for project: " + projectId));
    }

    public Group getGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found: " + groupId));
    }

    public List<MessageDTO> getGroupMessages(Long groupId, String userEmail) {
        Group group = getGroupById(groupId);
        
        // Check if user is a member of the project
        if (group.getProject() != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            
            boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(
                    group.getProject().getId(), user.getId());
            boolean isManager = group.getProject().getManager() != null && 
                    group.getProject().getManager().getId().equals(user.getId());
            
            if (!isMember && !isManager) {
                throw new ForbiddenException("You are not a member of this project");
            }
        }
        
        List<Message> messages = messageRepository.findByGroupIdOrderByCreatedAtAsc(groupId);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDTO sendMessage(Long groupId, String content, String senderEmail) {
        Group group = getGroupById(groupId);
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Check if user is a member of the project
        if (group.getProject() != null) {
            boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(
                    group.getProject().getId(), sender.getId());
            boolean isManager = group.getProject().getManager() != null && 
                    group.getProject().getManager().getId().equals(sender.getId());
            
            if (!isMember && !isManager) {
                throw new ForbiddenException("You are not a member of this project");
            }
        }

        Message message = new Message(content, sender, group);
        Message saved = messageRepository.save(message);
        return convertToDTO(saved);
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setGroupId(message.getGroup().getId());
        
        if (message.getSender() != null) {
            MessageDTO.SenderDTO senderDTO = new MessageDTO.SenderDTO();
            senderDTO.setId(message.getSender().getId());
            senderDTO.setName(message.getSender().getName());
            senderDTO.setEmail(message.getSender().getEmail());
            dto.setSender(senderDTO);
        }
        
        return dto;
    }
}

