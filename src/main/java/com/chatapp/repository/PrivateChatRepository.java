package com.chatapp.repository;

import com.chatapp.model.PrivateChat;
import com.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateChatRepository extends JpaRepository<PrivateChat, Long> {

    @Query("SELECT pc FROM PrivateChat pc WHERE (pc.user1 = :user1 AND pc.user2 = :user2) OR (pc.user1 = :user2 AND pc.user2 = :user1)")
    Optional<PrivateChat> findByUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT pc FROM PrivateChat pc WHERE pc.user1 = :user OR pc.user2 = :user")
    List<PrivateChat> findByUser(@Param("user") User user);
}
