package com.ecomm.userservice.controller;

import com.ecomm.userservice.dto.request.ChangePasswordRequest;
import com.ecomm.userservice.dto.request.UpdateUserRequest;
import com.ecomm.userservice.dto.response.UserResponse;
import com.ecomm.userservice.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    // GET user profile by ID
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        log.info("getUserById method called...");
        UserResponse response = userService.getUserById(userId);
        log.info("getUserById method calling completed");
        return ResponseEntity.ok(response);
    }

    // PUT update user profile
    @PutMapping("/profile/{userId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody UpdateUserRequest request,@PathVariable Long userId) {
       log.info("updateProfile method called...");
        UserResponse response = userService.updateProfile(request);
        log.info("updateProfile method calling completed...");
        return ResponseEntity.ok(response);
    }

    // POST change password
    @PostMapping("/change-password/{userId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request,@PathVariable Long userId) {
      log.info("changePassword method is calling...");
        userService.changePassword(request,userId);
        log.info("changePassword method calling completed....");
        return ResponseEntity.ok().build();
    }

    // DELETE deactivate account
    @DeleteMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deactivateAccount(@PathVariable Long userId) {
        log.info("deactivate method called....");
        userService.deactivateAccount(userId);
        log.info("deactivate method calling completed....");
        return ResponseEntity.noContent().build();
    }
}
