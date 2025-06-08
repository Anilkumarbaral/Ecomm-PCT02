package com.ecomm.userservice.service;

import com.ecomm.userservice.dto.request.LoginRequest;
import com.ecomm.userservice.dto.request.RegisterRequest;
import com.ecomm.userservice.dto.response.AuthResponse;

public interface AuthService {
    // Authentication
    public AuthResponse login(LoginRequest request);
    public AuthResponse register(RegisterRequest request);
    public void logout(String token);
    boolean isTokenInvalidated(String token);

    public AuthResponse refreshToken(String refreshToken);
    public void sendPasswordResetEmail(String email);
    public void resetPassword(String token, String newPassword);

}
