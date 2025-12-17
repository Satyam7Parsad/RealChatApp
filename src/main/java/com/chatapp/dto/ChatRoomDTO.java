package com.chatapp.dto;

import com.chatapp.model.ChatRoom;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatRoomDTO {
    private Long id;
    private String name;
    private Long createdById;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private Set<UserDTO> members;

    public ChatRoomDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<UserDTO> getMembers() { return members; }
    public void setMembers(Set<UserDTO> members) { this.members = members; }

    public static ChatRoomDTO fromChatRoom(ChatRoom chatRoom) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .createdById(chatRoom.getCreatedBy() != null ? chatRoom.getCreatedBy().getId() : null)
                .createdByUsername(chatRoom.getCreatedBy() != null ? chatRoom.getCreatedBy().getUsername() : null)
                .createdAt(chatRoom.getCreatedAt())
                .members(chatRoom.getMembers().stream()
                        .map(UserDTO::fromUser)
                        .collect(Collectors.toSet()))
                .build();
    }

    // Builder
    public static ChatRoomDTOBuilder builder() { return new ChatRoomDTOBuilder(); }

    public static class ChatRoomDTOBuilder {
        private Long id;
        private String name;
        private Long createdById;
        private String createdByUsername;
        private LocalDateTime createdAt;
        private Set<UserDTO> members;

        public ChatRoomDTOBuilder id(Long id) { this.id = id; return this; }
        public ChatRoomDTOBuilder name(String name) { this.name = name; return this; }
        public ChatRoomDTOBuilder createdById(Long createdById) { this.createdById = createdById; return this; }
        public ChatRoomDTOBuilder createdByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; return this; }
        public ChatRoomDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ChatRoomDTOBuilder members(Set<UserDTO> members) { this.members = members; return this; }

        public ChatRoomDTO build() {
            ChatRoomDTO dto = new ChatRoomDTO();
            dto.id = this.id;
            dto.name = this.name;
            dto.createdById = this.createdById;
            dto.createdByUsername = this.createdByUsername;
            dto.createdAt = this.createdAt;
            dto.members = this.members;
            return dto;
        }
    }
}
