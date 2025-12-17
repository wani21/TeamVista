package com.productivity.dashboard.controller;

import com.productivity.dashboard.dto.MessageDTO;
import com.productivity.dashboard.model.Group;
import com.productivity.dashboard.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Get the group associated with a project
     */
    @GetMapping("/projects/{projectId}/group")
    public ResponseEntity<Map<String, Object>> getProjectGroup(@PathVariable Long projectId) {
        Group group = chatService.getGroupByProjectId(projectId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", group.getId());
        response.put("name", group.getName());
        response.put("type", group.getType());
        response.put("projectId", group.getProject() != null ? group.getProject().getId() : null);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all messages in a group
     */
    @GetMapping("/groups/{groupId}/messages")
    public ResponseEntity<List<MessageDTO>> getGroupMessages(
            @PathVariable Long groupId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<MessageDTO> messages = chatService.getGroupMessages(groupId, userEmail);
        return ResponseEntity.ok(messages);
    }

    /**
     * Send a message to a group
     */
    @PostMapping(value = "/groups/{groupId}/messages", consumes = "text/plain")
    public ResponseEntity<MessageDTO> sendGroupMessage(
            @PathVariable Long groupId,
            @RequestBody String content,
            Authentication authentication) {
        String senderEmail = authentication.getName();
        MessageDTO message = chatService.sendMessage(groupId, content, senderEmail);
        return ResponseEntity.ok(message);
    }

    /**
     * Get group details by ID
     */
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<Map<String, Object>> getGroup(@PathVariable Long groupId) {
        Group group = chatService.getGroupById(groupId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", group.getId());
        response.put("name", group.getName());
        response.put("type", group.getType());
        response.put("projectId", group.getProject() != null ? group.getProject().getId() : null);
        
        return ResponseEntity.ok(response);
    }
}

