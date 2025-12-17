package com.chatapp.repository;

import com.chatapp.model.ChatRoom;
import com.chatapp.model.Message;
import com.chatapp.model.PrivateChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.chatRoom = :chatRoom AND m.messageType = 'CHAT' ORDER BY m.createdAt DESC")
    Page<Message> findByChatRoomOrderByCreatedAtDesc(@Param("chatRoom") ChatRoom chatRoom, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.privateChat = :privateChat AND m.messageType = 'CHAT' ORDER BY m.createdAt DESC")
    Page<Message> findByPrivateChatOrderByCreatedAtDesc(@Param("privateChat") PrivateChat privateChat, Pageable pageable);

    List<Message> findTop50ByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);

    List<Message> findTop50ByPrivateChatOrderByCreatedAtAsc(PrivateChat privateChat);
}
