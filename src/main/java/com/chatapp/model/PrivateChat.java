package com.chatapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "private_chats")
public class PrivateChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "privateChat", cascade = CascadeType.ALL)
    private Set<Message> messages = new HashSet<>();

    public PrivateChat() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser1() { return user1; }
    public void setUser1(User user1) { this.user1 = user1; }

    public User getUser2() { return user2; }
    public void setUser2(User user2) { this.user2 = user2; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<Message> getMessages() { return messages; }
    public void setMessages(Set<Message> messages) { this.messages = messages; }

    // Builder pattern
    public static PrivateChatBuilder builder() { return new PrivateChatBuilder(); }

    public static class PrivateChatBuilder {
        private Long id;
        private User user1;
        private User user2;
        private LocalDateTime createdAt = LocalDateTime.now();

        public PrivateChatBuilder id(Long id) { this.id = id; return this; }
        public PrivateChatBuilder user1(User user1) { this.user1 = user1; return this; }
        public PrivateChatBuilder user2(User user2) { this.user2 = user2; return this; }
        public PrivateChatBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public PrivateChat build() {
            PrivateChat chat = new PrivateChat();
            chat.id = this.id;
            chat.user1 = this.user1;
            chat.user2 = this.user2;
            chat.createdAt = this.createdAt;
            return chat;
        }
    }
}
