package com.chatapp.service;

import com.chatapp.dto.ChatRoomDTO;
import com.chatapp.dto.CreateRoomRequest;
import com.chatapp.model.ChatRoom;
import com.chatapp.model.User;
import com.chatapp.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserService userService;

    public ChatRoom findById(Long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
    }

    public List<ChatRoomDTO> getAllRooms() {
        return chatRoomRepository.findAll().stream()
                .map(ChatRoomDTO::fromChatRoom)
                .collect(Collectors.toList());
    }

    public List<ChatRoomDTO> getUserRooms(String username) {
        User user = userService.findByUsername(username);
        return chatRoomRepository.findByMember(user).stream()
                .map(ChatRoomDTO::fromChatRoom)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatRoomDTO createRoom(CreateRoomRequest request, String username) {
        User creator = userService.findByUsername(username);

        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.getName())
                .createdBy(creator)
                .build();

        chatRoom.getMembers().add(creator);
        chatRoomRepository.save(chatRoom);

        return ChatRoomDTO.fromChatRoom(chatRoom);
    }

    @Transactional
    public ChatRoomDTO joinRoom(Long roomId, String username) {
        ChatRoom chatRoom = findById(roomId);
        User user = userService.findByUsername(username);

        chatRoom.getMembers().add(user);
        chatRoomRepository.save(chatRoom);

        return ChatRoomDTO.fromChatRoom(chatRoom);
    }

    @Transactional
    public void leaveRoom(Long roomId, String username) {
        ChatRoom chatRoom = findById(roomId);
        User user = userService.findByUsername(username);

        chatRoom.getMembers().remove(user);
        chatRoomRepository.save(chatRoom);
    }

    public ChatRoomDTO getRoomWithMembers(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByIdWithMembers(roomId);
        if (chatRoom == null) {
            throw new RuntimeException("Chat room not found");
        }
        return ChatRoomDTO.fromChatRoom(chatRoom);
    }
}
