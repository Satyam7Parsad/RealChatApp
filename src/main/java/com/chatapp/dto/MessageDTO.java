package com.chatapp.dto;

import com.chatapp.model.Message;
import java.time.LocalDateTime;

public class MessageDTO {
    private Long id;
    private String content;
    private Long senderId;
    private String senderUsername;
    private Long roomId;
    private Long privateChatId;
    private String messageType;
    private LocalDateTime createdAt;

    public MessageDTO() {}

    public MessageDTO(Long id, String content, Long senderId, String senderUsername,
                     Long roomId, Long privateChatId, String messageType, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.roomId = roomId;
        this.privateChatId = privateChatId;
        this.messageType = messageType;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getPrivateChatId() { return privateChatId; }
    public void setPrivateChatId(Long privateChatId) { this.privateChatId = privateChatId; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static MessageDTO fromMessage(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .roomId(message.getChatRoom() != null ? message.getChatRoom().getId() : null)
                .privateChatId(message.getPrivateChat() != null ? message.getPrivateChat().getId() : null)
                .messageType(message.getMessageType().name())
                .createdAt(message.getCreatedAt())
                .build();
    }

    // Builder
    public static MessageDTOBuilder builder() { return new MessageDTOBuilder(); }

    public static class MessageDTOBuilder {
        private Long id;
        private String content;
        private Long senderId;
        private String senderUsername;
        private Long roomId;
        private Long privateChatId;
        private String messageType;
        private LocalDateTime createdAt;

        public MessageDTOBuilder id(Long id) { this.id = id; return this; }
        public MessageDTOBuilder content(String content) { this.content = content; return this; }
        public MessageDTOBuilder senderId(Long senderId) { this.senderId = senderId; return this; }
        public MessageDTOBuilder senderUsername(String senderUsername) { this.senderUsername = senderUsername; return this; }
        public MessageDTOBuilder roomId(Long roomId) { this.roomId = roomId; return this; }
        public MessageDTOBuilder privateChatId(Long privateChatId) { this.privateChatId = privateChatId; return this; }
        public MessageDTOBuilder messageType(String messageType) { this.messageType = messageType; return this; }
        public MessageDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public MessageDTO build() {
            return new MessageDTO(id, content, senderId, senderUsername, roomId, privateChatId, messageType, createdAt);
        }
    }
}
