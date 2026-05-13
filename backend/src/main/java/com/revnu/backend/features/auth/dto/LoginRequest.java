package com.revnu.backend.features.auth.dto;

public record LoginRequest(
    String email,
    String password
) {}
