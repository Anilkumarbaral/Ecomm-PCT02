package com.auth.service;

import com.auth.dto.ChangePasswordRequest;
import com.auth.dto.UpdateUserRequest;
import com.auth.dto.UserDto;

public interface UserService {
    UserDto getUserById(Long id);
    UserDto updateProfile(String username, UpdateUserRequest request);
    void changePassword(String username, ChangePasswordRequest request);
    void deactivateAccount(String username);
}
