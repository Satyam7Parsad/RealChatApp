package com.chatapp.controller;

import com.chatapp.dto.MessageDTO;
import com.chatapp.dto.PrivateChatDTO;
import com.chatapp.service.MessageService;
import com.chatapp.service.PrivateChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/private")
@CrossOrigin(origins = "*")
public class PrivateChatController {

    @Autowired
    private PrivateChatService privateChatService;

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<List<PrivateChatDTO>> getMyPrivateChats(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(privateChatService.getUserPrivateChats(userDetails.getUsername()));
    }

    @PostMapping("/start/{userId}")
    public ResponseEntity<PrivateChatDTO> startPrivateChat(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(privateChatService.startPrivateChat(userDetails.getUsername(), userId));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageDTO>> getPrivateChatMessages(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(messageService.getPrivateChatMessages(id, page, size));
    }

    @GetMapping("/{id}/messages/recent")
    public ResponseEntity<List<MessageDTO>> getRecentMessages(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getRecentPrivateMessages(id));
    }
}
