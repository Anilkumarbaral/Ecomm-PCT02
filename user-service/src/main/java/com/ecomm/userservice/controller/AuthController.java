package com.ecomm.userservice.controller;


import com.ecomm.userservice.dto.request.LoginRequest;
import com.ecomm.userservice.dto.request.RegisterRequest;
import com.ecomm.userservice.dto.request.ResetPasswordRequest;
import com.ecomm.userservice.dto.response.AuthResponse;
import com.ecomm.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        // Expecting Authorization header like: "Bearer <token>"
        String token = authorizationHeader.replace("Bearer ", "");
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/password-reset-request")
//    public ResponseEntity<Void> sendPasswordResetEmail(@RequestParam String email) {
//        authService.sendPasswordResetEmail(email);
//        return ResponseEntity.noContent().build();
//    }

    @PostMapping("/password-reset")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.noContent().
                build();
    }
}
