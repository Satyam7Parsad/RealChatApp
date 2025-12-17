package com.chatapp.repository;

import com.chatapp.model.ChatRoom;
import com.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m = :user")
    List<ChatRoom> findByMember(@Param("user") User user);

    List<ChatRoom> findByCreatedBy(User user);

    @Query("SELECT cr FROM ChatRoom cr LEFT JOIN FETCH cr.members WHERE cr.id = :id")
    ChatRoom findByIdWithMembers(@Param("id") Long id);
}
