package com.chatapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "private_chat_id")
    private PrivateChat privateChat;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.CHAT;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum MessageType {
        CHAT, JOIN, LEAVE, TYPING
    }

    public Message() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public ChatRoom getChatRoom() { return chatRoom; }
    public void setChatRoom(ChatRoom chatRoom) { this.chatRoom = chatRoom; }

    public PrivateChat getPrivateChat() { return privateChat; }
    public void setPrivateChat(PrivateChat privateChat) { this.privateChat = privateChat; }

    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder pattern
    public static MessageBuilder builder() { return new MessageBuilder(); }

    public static class MessageBuilder {
        private Long id;
        private String content;
        private User sender;
        private ChatRoom chatRoom;
        private PrivateChat privateChat;
        private MessageType messageType = MessageType.CHAT;
        private LocalDateTime createdAt = LocalDateTime.now();

        public MessageBuilder id(Long id) { this.id = id; return this; }
        public MessageBuilder content(String content) { this.content = content; return this; }
        public MessageBuilder sender(User sender) { this.sender = sender; return this; }
        public MessageBuilder chatRoom(ChatRoom chatRoom) { this.chatRoom = chatRoom; return this; }
        public MessageBuilder privateChat(PrivateChat privateChat) { this.privateChat = privateChat; return this; }
        public MessageBuilder messageType(MessageType messageType) { this.messageType = messageType; return this; }
        public MessageBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Message build() {
            Message message = new Message();
            message.id = this.id;
            message.content = this.content;
            message.sender = this.sender;
            message.chatRoom = this.chatRoom;
            message.privateChat = this.privateChat;
            message.messageType = this.messageType;
            message.createdAt = this.createdAt;
            return message;
        }
    }
}
