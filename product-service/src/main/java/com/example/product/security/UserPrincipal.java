package com.example.product.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserPrincipal {
    private String userId;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(String userId, String email, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.authorities = authorities;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    public boolean hasRole(String role) {
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role.toUpperCase()));
    }
}