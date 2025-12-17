package com.chatapp.config;

import com.chatapp.dto.UserDTO;
import com.chatapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    // Track connected users by session ID
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        logger.info("New WebSocket connection: {}", sessionId);
    }

    public void registerUser(String sessionId, String username) {
        sessionUserMap.put(sessionId, username);
        userService.setUserOnline(username);
        broadcastOnlineStatus();
        logger.info("User {} connected", username);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        String username = sessionUserMap.remove(sessionId);
        if (username != null) {
            userService.setUserOffline(username);
            broadcastOnlineStatus();
            logger.info("User {} disconnected", username);
        }
    }

    private void broadcastOnlineStatus() {
        messagingTemplate.convertAndSend("/topic/online", userService.getOnlineUsers());
    }

    public void broadcastTyping(Long roomId, UserDTO user, boolean isTyping) {
        Map<String, Object> typingNotification = Map.of(
                "user", user,
                "roomId", roomId,
                "isTyping", isTyping
        );
        messagingTemplate.convertAndSend("/topic/typing/" + roomId, typingNotification);
    }
}
