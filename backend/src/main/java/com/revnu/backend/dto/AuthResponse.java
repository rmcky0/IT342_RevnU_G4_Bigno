package com.revnu.backend.dto;

import com.revnu.backend.model.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private String email;
    private String fullName;
    private RoleType role;
}