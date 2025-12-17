package com.chatapp.controller;

import com.chatapp.dto.ChatRoomDTO;
import com.chatapp.dto.CreateRoomRequest;
import com.chatapp.dto.MessageDTO;
import com.chatapp.service.ChatRoomService;
import com.chatapp.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getAllRooms() {
        return ResponseEntity.ok(chatRoomService.getAllRooms());
    }

    @GetMapping("/my")
    public ResponseEntity<List<ChatRoomDTO>> getMyRooms(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatRoomService.getUserRooms(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoomDTO> getRoom(@PathVariable Long id) {
        return ResponseEntity.ok(chatRoomService.getRoomWithMembers(id));
    }

    @PostMapping
    public ResponseEntity<ChatRoomDTO> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatRoomService.createRoom(request, userDetails.getUsername()));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<ChatRoomDTO> joinRoom(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatRoomService.joinRoom(id, userDetails.getUsername()));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveRoom(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        chatRoomService.leaveRoom(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageDTO>> getRoomMessages(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(messageService.getRoomMessages(id, page, size));
    }

    @GetMapping("/{id}/messages/recent")
    public ResponseEntity<List<MessageDTO>> getRecentMessages(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getRecentRoomMessages(id));
    }
}
