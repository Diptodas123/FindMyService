package com.FindMyService.service;

import com.FindMyService.model.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    public List<User> getAllUsers() {
        return Collections.emptyList();
    }

    public Optional<User> getUserById(String id) {
        return Optional.empty();
    }

    public User createUser(User user) {
        return user;
    }

    public Optional<User> updateUser(String id, User user) {
        return Optional.empty();
    }

    public boolean deleteUser(String id) {
        return false;
    }
}
