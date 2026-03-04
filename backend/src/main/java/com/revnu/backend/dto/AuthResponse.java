package com.revnu.backend.dto;

import com.revnu.backend.model.RoleType;

public class AuthResponse {
    private String message;
    private String email;
    private String fullName;
    private RoleType role;

    public AuthResponse(String message, String email, String fullName, RoleType role) {
        this.message = message;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public String getMessage() { return message; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public RoleType getRole() { return role; }
}