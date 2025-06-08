package com.ecomm.userservice.service;

import com.ecomm.userservice.dto.request.ChangePasswordRequest;
import com.ecomm.userservice.dto.request.UpdateUserRequest;
import com.ecomm.userservice.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetails;


public interface UserService {

    // Profile Management
    public UserResponse getUserById(Long userId);
    public UserResponse updateProfile(UpdateUserRequest request);
    public void changePassword(ChangePasswordRequest request, Long userId);
    public void deactivateAccount(Long userId);


    UserDetails loadUserByUsername(String username);
}