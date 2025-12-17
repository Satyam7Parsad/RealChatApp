package com.chatapp.service;

import com.chatapp.dto.PrivateChatDTO;
import com.chatapp.model.PrivateChat;
import com.chatapp.model.User;
import com.chatapp.repository.PrivateChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrivateChatService {

    @Autowired
    private PrivateChatRepository privateChatRepository;

    @Autowired
    private UserService userService;

    public PrivateChat findById(Long id) {
        return privateChatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Private chat not found"));
    }

    public List<PrivateChatDTO> getUserPrivateChats(String username) {
        User user = userService.findByUsername(username);
        return privateChatRepository.findByUser(user).stream()
                .map(PrivateChatDTO::fromPrivateChat)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrivateChat getOrCreatePrivateChat(User user1, User user2) {
        return privateChatRepository.findByUsers(user1, user2)
                .orElseGet(() -> {
                    PrivateChat newChat = PrivateChat.builder()
                            .user1(user1)
                            .user2(user2)
                            .build();
                    return privateChatRepository.save(newChat);
                });
    }

    @Transactional
    public PrivateChatDTO startPrivateChat(String currentUsername, Long otherUserId) {
        User currentUser = userService.findByUsername(currentUsername);
        User otherUser = userService.findById(otherUserId);

        PrivateChat privateChat = getOrCreatePrivateChat(currentUser, otherUser);
        return PrivateChatDTO.fromPrivateChat(privateChat);
    }
}
