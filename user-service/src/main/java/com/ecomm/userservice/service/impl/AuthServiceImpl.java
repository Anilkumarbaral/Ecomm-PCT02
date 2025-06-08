package com.ecomm.userservice.service.impl;

import com.ecomm.userservice.dto.request.LoginRequest;
import com.ecomm.userservice.dto.request.RegisterRequest;
import com.ecomm.userservice.dto.response.AuthResponse;
import com.ecomm.userservice.model.Role;
import com.ecomm.userservice.model.User;
import com.ecomm.userservice.repository.RoleRepository;
import com.ecomm.userservice.repository.UserRepository;
import com.ecomm.userservice.service.AuthService;
import com.ecomm.userservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService tokenService;
    @Autowired
    private  RoleRepository roleRepository;

    // In-memory storage for invalidated tokens and password reset tokens
    private final Set<String> invalidatedTokens = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, String> passwordResetTokens = Collections.synchronizedMap(new HashMap<>());

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("User account is not active");
        }
        Role roles = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("User role not found"));
       // Role role = roleRepository.findByUsers_Id(user.getId()).get();
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .role(roles.getRoleName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        // Fetch default role
        Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .status("ACTIVE")
                .roles(Collections.singleton(customerRole))
                .build();

        User savedUser = userRepository.save(user);

        String accessToken = tokenService.generateAccessToken(savedUser);
        String refreshToken = tokenService.generateRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    public void logout(String token) {
        invalidateToken(token);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!isValidRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        User user = getUserFromRefreshToken(refreshToken);

        String newAccessToken = tokenService.generateAccessToken(user);
        String newRefreshToken = tokenService.generateRefreshToken(user);

        // Invalidate old refresh token to prevent reuse
        invalidateToken(refreshToken);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }

    @Override
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String resetToken = UUID.randomUUID().toString();

        // Store token linked to user email
        storePasswordResetToken(user, resetToken);

        // TODO: Integrate your email service to send resetToken to user email
        System.out.println("Password reset token for " + email + ": " + resetToken);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = validatePasswordResetToken(token);

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        invalidatePasswordResetToken(token);
    }

    // --- Helper methods below ---

    private void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }

    private boolean isValidRefreshToken(String token) {
        if (isTokenInvalidated(token)) return false;

        try {
            String user = tokenService.extractUsername(token);
            User user1 = userRepository.findByEmail(user)
                    .orElseThrow(() -> new RuntimeException("User not found for refresh token"));
            if (!tokenService.validateToken(token, (UserDetails) user1)) return false;

            var claims = tokenService.extractAllClaims(token);
            String type = claims.get("type", String.class);
            return "REFRESH".equals(type) && !tokenService.isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private User getUserFromRefreshToken(String token) {
        String email = tokenService.extractUsername(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for refresh token"));
    }

    private void storePasswordResetToken(User user, String token) {
        passwordResetTokens.put(token, user.getEmail());
    }

    private User validatePasswordResetToken(String token) {
        String email = passwordResetTokens.get(token);
        if (email == null) {
            throw new RuntimeException("Invalid or expired password reset token");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void invalidatePasswordResetToken(String token) {
        passwordResetTokens.remove(token);
    }
}
