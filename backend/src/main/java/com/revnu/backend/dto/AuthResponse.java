package com.revnu.backend.dto;

import com.revnu.backend.model.RoleType;
import com.revnu.backend.model.UserStatus;

public class AuthResponse {
    private String message;
    private String email;
    private String fullName;
    private RoleType role;
    private UserStatus status;
    private String accessToken;

    public AuthResponse(String message, String email, String fullName, RoleType role, UserStatus status) {
        this(message, email, fullName, role, status, null);
    }

    public AuthResponse(String message, String email, String fullName, RoleType role, UserStatus status, String accessToken) {
        this.message = message;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.accessToken = accessToken;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public RoleType getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getAccessToken() {
        return accessToken;
    }
}