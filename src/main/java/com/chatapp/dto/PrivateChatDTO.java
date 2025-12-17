package com.chatapp.dto;

import com.chatapp.model.PrivateChat;
import java.time.LocalDateTime;

public class PrivateChatDTO {
    private Long id;
    private UserDTO user1;
    private UserDTO user2;
    private LocalDateTime createdAt;

    public PrivateChatDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDTO getUser1() { return user1; }
    public void setUser1(UserDTO user1) { this.user1 = user1; }

    public UserDTO getUser2() { return user2; }
    public void setUser2(UserDTO user2) { this.user2 = user2; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static PrivateChatDTO fromPrivateChat(PrivateChat privateChat) {
        return PrivateChatDTO.builder()
                .id(privateChat.getId())
                .user1(UserDTO.fromUser(privateChat.getUser1()))
                .user2(UserDTO.fromUser(privateChat.getUser2()))
                .createdAt(privateChat.getCreatedAt())
                .build();
    }

    // Builder
    public static PrivateChatDTOBuilder builder() { return new PrivateChatDTOBuilder(); }

    public static class PrivateChatDTOBuilder {
        private Long id;
        private UserDTO user1;
        private UserDTO user2;
        private LocalDateTime createdAt;

        public PrivateChatDTOBuilder id(Long id) { this.id = id; return this; }
        public PrivateChatDTOBuilder user1(UserDTO user1) { this.user1 = user1; return this; }
        public PrivateChatDTOBuilder user2(UserDTO user2) { this.user2 = user2; return this; }
        public PrivateChatDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public PrivateChatDTO build() {
            PrivateChatDTO dto = new PrivateChatDTO();
            dto.id = this.id;
            dto.user1 = this.user1;
            dto.user2 = this.user2;
            dto.createdAt = this.createdAt;
            return dto;
        }
    }
}
