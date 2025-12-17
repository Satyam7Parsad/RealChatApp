package com.chatapp.service;

import com.chatapp.dto.UserDTO;
import com.chatapp.model.User;
import com.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getOnlineUsers() {
        return userRepository.findByStatus(User.UserStatus.ONLINE).stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    @Transactional
    public void setUserOnline(String username) {
        User user = findByUsername(username);
        user.setStatus(User.UserStatus.ONLINE);
        userRepository.save(user);
    }

    @Transactional
    public void setUserOffline(String username) {
        User user = findByUsername(username);
        user.setStatus(User.UserStatus.OFFLINE);
        userRepository.save(user);
    }

    public UserDTO getUserProfile(String username) {
        User user = findByUsername(username);
        return UserDTO.fromUser(user);
    }

    public UserDTO findByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found with this phone number"));
        return UserDTO.fromUser(user);
    }
}
