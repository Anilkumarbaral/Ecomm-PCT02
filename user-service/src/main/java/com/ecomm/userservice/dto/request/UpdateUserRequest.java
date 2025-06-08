package com.ecomm.userservice.dto.request;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    private Long userId; // Required to identify which user to update
    private String firstName;
    private String lastName;
    private String phone;
    private String email; // Optional: include if email is editable
}
