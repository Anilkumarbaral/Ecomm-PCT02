package com.auth.controller;

import com.auth.dto.ChangePasswordRequest;
import com.auth.dto.UpdateUserRequest;
import com.auth.dto.UserDto;
import com.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody UpdateUserRequest request, Principal principal) {
        return ResponseEntity.ok(userService.updateProfile(principal.getName(), request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request, Principal principal) {
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Password changed successfully.");
    }

    @DeleteMapping("/deactivate")
    public ResponseEntity<String> deactivateAccount(Principal principal) {
        userService.deactivateAccount(principal.getName());
        return ResponseEntity.ok("Account deactivated.");
    }
}

