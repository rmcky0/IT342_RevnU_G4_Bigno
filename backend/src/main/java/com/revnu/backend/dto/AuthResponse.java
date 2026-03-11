package com.revnu.backend.dto;

import com.revnu.backend.model.RoleType;

public class AuthResponse {
    private String message;
    private String email;
    private String fullName;
    private RoleType role;
    private String accessToken;

    /** Standard login/register response (no token). */
    public AuthResponse(String message, String email, String fullName, RoleType role) {
        this(message, email, fullName, role, null);
    }

    /** OAuth2 / JWT response (includes token). */
    public AuthResponse(String message, String email, String fullName, RoleType role, String accessToken) {
        this.message = message;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.accessToken = accessToken;
    }

    public String getMessage() { return message; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public RoleType getRole() { return role; }
    public String getAccessToken() { return accessToken; }
}