package com.chatapp.dto;

public class ChatMessageRequest {
    private String content;
    private Long roomId;
    private Long recipientId;
    private String type; // CHAT, TYPING, JOIN, LEAVE

    public ChatMessageRequest() {}

    public ChatMessageRequest(String content, Long roomId, Long recipientId, String type) {
        this.content = content;
        this.roomId = roomId;
        this.recipientId = recipientId;
        this.type = type;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
