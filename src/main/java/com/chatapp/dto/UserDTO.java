package com.chatapp.dto;

import com.chatapp.model.User;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String status;

    public UserDTO() {}

    public UserDTO(Long id, String username, String email, String phoneNumber, String status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static UserDTO fromUser(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus().name())
                .build();
    }

    // Builder
    public static UserDTOBuilder builder() { return new UserDTOBuilder(); }

    public static class UserDTOBuilder {
        private Long id;
        private String username;
        private String email;
        private String phoneNumber;
        private String status;

        public UserDTOBuilder id(Long id) { this.id = id; return this; }
        public UserDTOBuilder username(String username) { this.username = username; return this; }
        public UserDTOBuilder email(String email) { this.email = email; return this; }
        public UserDTOBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public UserDTOBuilder status(String status) { this.status = status; return this; }

        public UserDTO build() {
            return new UserDTO(id, username, email, phoneNumber, status);
        }
    }
}
