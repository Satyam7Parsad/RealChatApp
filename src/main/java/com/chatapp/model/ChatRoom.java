package com.chatapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(
        name = "room_members",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private Set<Message> messages = new HashSet<>();

    public ChatRoom() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<User> getMembers() { return members; }
    public void setMembers(Set<User> members) { this.members = members; }

    public Set<Message> getMessages() { return messages; }
    public void setMessages(Set<Message> messages) { this.messages = messages; }

    // Builder pattern
    public static ChatRoomBuilder builder() { return new ChatRoomBuilder(); }

    public static class ChatRoomBuilder {
        private Long id;
        private String name;
        private User createdBy;
        private LocalDateTime createdAt = LocalDateTime.now();
        private Set<User> members = new HashSet<>();

        public ChatRoomBuilder id(Long id) { this.id = id; return this; }
        public ChatRoomBuilder name(String name) { this.name = name; return this; }
        public ChatRoomBuilder createdBy(User createdBy) { this.createdBy = createdBy; return this; }
        public ChatRoomBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ChatRoomBuilder members(Set<User> members) { this.members = members; return this; }

        public ChatRoom build() {
            ChatRoom room = new ChatRoom();
            room.id = this.id;
            room.name = this.name;
            room.createdBy = this.createdBy;
            room.createdAt = this.createdAt;
            room.members = this.members;
            return room;
        }
    }
}
