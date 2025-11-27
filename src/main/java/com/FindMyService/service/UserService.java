package com.FindMyService.service;

import com.FindMyService.model.User;
import com.FindMyService.repository.UserRepository;
import com.FindMyService.utils.ErrorResponseBuilder;
import com.FindMyService.utils.OwnerCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, OwnerCheck ownerCheck) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public ResponseEntity<?> createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponseBuilder.build(HttpStatus.BAD_REQUEST, "Email is required"));
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponseBuilder.build(HttpStatus.BAD_REQUEST, "Password is required"));
        }

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User created = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponseBuilder.serverError("Failed to create user: " + e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> updateUser(Long userId, User user) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, "User not found"));
        }

        try {
            user.setUserId(userId);
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                user.setPassword(existingUser.get().getPassword());
            }
            User updated = userRepository.save(user);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponseBuilder.serverError("Failed to update user: " + e.getMessage()));
        }
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        return userRepository.findById(userId).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }
}
