package com.chatapp.service;

import com.chatapp.dto.ChatMessageRequest;
import com.chatapp.dto.MessageDTO;
import com.chatapp.model.ChatRoom;
import com.chatapp.model.Message;
import com.chatapp.model.PrivateChat;
import com.chatapp.model.User;
import com.chatapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private PrivateChatService privateChatService;

    @Transactional
    public MessageDTO saveRoomMessage(ChatMessageRequest request, String senderUsername) {
        User sender = userService.findByUsername(senderUsername);
        ChatRoom chatRoom = chatRoomService.findById(request.getRoomId());

        Message message = Message.builder()
                .content(request.getContent())
                .sender(sender)
                .chatRoom(chatRoom)
                .messageType(Message.MessageType.valueOf(request.getType() != null ? request.getType() : "CHAT"))
                .build();

        messageRepository.save(message);
        return MessageDTO.fromMessage(message);
    }

    @Transactional
    public MessageDTO savePrivateMessage(ChatMessageRequest request, String senderUsername) {
        User sender = userService.findByUsername(senderUsername);
        User recipient = userService.findById(request.getRecipientId());

        PrivateChat privateChat = privateChatService.getOrCreatePrivateChat(sender, recipient);

        Message message = Message.builder()
                .content(request.getContent())
                .sender(sender)
                .privateChat(privateChat)
                .messageType(Message.MessageType.CHAT)
                .build();

        messageRepository.save(message);
        return MessageDTO.fromMessage(message);
    }

    public List<MessageDTO> getRoomMessages(Long roomId, int page, int size) {
        ChatRoom chatRoom = chatRoomService.findById(roomId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom, pageable);

        return messages.getContent().stream()
                .map(MessageDTO::fromMessage)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getPrivateChatMessages(Long privateChatId, int page, int size) {
        PrivateChat privateChat = privateChatService.findById(privateChatId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findByPrivateChatOrderByCreatedAtDesc(privateChat, pageable);

        return messages.getContent().stream()
                .map(MessageDTO::fromMessage)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getRecentRoomMessages(Long roomId) {
        ChatRoom chatRoom = chatRoomService.findById(roomId);
        return messageRepository.findTop50ByChatRoomOrderByCreatedAtAsc(chatRoom).stream()
                .map(MessageDTO::fromMessage)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getRecentPrivateMessages(Long privateChatId) {
        PrivateChat privateChat = privateChatService.findById(privateChatId);
        return messageRepository.findTop50ByPrivateChatOrderByCreatedAtAsc(privateChat).stream()
                .map(MessageDTO::fromMessage)
                .collect(Collectors.toList());
    }
}
