package com.FindMyService.controller;

import java.util.List;
import java.util.stream.Collectors;
import com.FindMyService.model.dto.UserDto;
import com.FindMyService.utils.DtoMapper;
import com.FindMyService.utils.ErrorResponseBuilder;
import com.FindMyService.utils.OwnerCheck;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.FindMyService.model.User;
import com.FindMyService.service.UserService;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;
    private final OwnerCheck ownerCheck;

    public UserController(UserService userService, OwnerCheck ownerCheck) {
        this.userService = userService;
        this.ownerCheck = ownerCheck;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> dtos = userService.getAllUsers()
                .stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        try {
            ownerCheck.verifyOwner(userId);
        } catch (AccessDeniedException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponseBuilder.forbidden("You are not authorized to access this user"));
        }
        return userService.getUserById(userId)
                .map(DtoMapper::toDto)
                .map(dto -> ResponseEntity.ok((Object) dto))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, "User not found")));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user) {
        try {
            ownerCheck.verifyOwner(userId);
        } catch (AccessDeniedException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponseBuilder.forbidden("You are not authorized to update this user"));
        }

        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            ownerCheck.verifyOwner(userId);
        } catch (AccessDeniedException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponseBuilder.forbidden("You are not authorized to delete this user"));
        }
        if (userId == null || userId <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid userId"));
        }

        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            return ResponseEntity.ok(ErrorResponseBuilder.ok("User with id " + userId + " deleted successfully"));
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, "User not found"));
    }
}
