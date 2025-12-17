package com.chatapp.controller;

import com.chatapp.config.WebSocketEventListener;
import com.chatapp.dto.ChatMessageRequest;
import com.chatapp.dto.MessageDTO;
import com.chatapp.dto.UserDTO;
import com.chatapp.service.MessageService;
import com.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private WebSocketEventListener webSocketEventListener;

    // Register user on connect
    @MessageMapping("/chat.register")
    public void registerUser(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String username = payload.get("username");
        String sessionId = headerAccessor.getSessionId();
        headerAccessor.getSessionAttributes().put("username", username);
        webSocketEventListener.registerUser(sessionId, username);

        // Send online users list
        messagingTemplate.convertAndSend("/topic/online", userService.getOnlineUsers());
    }

    // Send message to a room
    @MessageMapping("/chat.room.{roomId}")
    public void sendRoomMessage(@DestinationVariable Long roomId,
                                @Payload ChatMessageRequest request,
                                SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if ("TYPING".equals(request.getType())) {
            // Handle typing indicator
            UserDTO user = userService.getUserProfile(username);
            webSocketEventListener.broadcastTyping(roomId, user, true);
        } else {
            // Save and broadcast message
            request.setRoomId(roomId);
            request.setType("CHAT");
            MessageDTO message = messageService.saveRoomMessage(request, username);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
        }
    }

    // Send private message
    @MessageMapping("/chat.private.{recipientId}")
    public void sendPrivateMessage(@DestinationVariable Long recipientId,
                                   @Payload ChatMessageRequest request,
                                   SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        request.setRecipientId(recipientId);

        MessageDTO message = messageService.savePrivateMessage(request, username);

        // Send to sender
        UserDTO sender = userService.getUserProfile(username);
        messagingTemplate.convertAndSend("/queue/private/" + sender.getId(), message);

        // Send to recipient
        messagingTemplate.convertAndSend("/queue/private/" + recipientId, message);
    }

    // Join room notification
    @MessageMapping("/chat.join.{roomId}")
    public void joinRoom(@DestinationVariable Long roomId,
                         SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        UserDTO user = userService.getUserProfile(username);

        Map<String, Object> notification = Map.of(
                "type", "JOIN",
                "user", user,
                "roomId", roomId
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomId, notification);
    }

    // Leave room notification
    @MessageMapping("/chat.leave.{roomId}")
    public void leaveRoom(@DestinationVariable Long roomId,
                          SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        UserDTO user = userService.getUserProfile(username);

        Map<String, Object> notification = Map.of(
                "type", "LEAVE",
                "user", user,
                "roomId", roomId
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomId, notification);
    }

    // Stop typing indicator
    @MessageMapping("/chat.stopTyping.{roomId}")
    public void stopTyping(@DestinationVariable Long roomId,
                           SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        UserDTO user = userService.getUserProfile(username);
        webSocketEventListener.broadcastTyping(roomId, user, false);
    }
}
