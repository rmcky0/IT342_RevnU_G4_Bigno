package com.revnu.backend.features.auth.dto;

public record RegisterRequest(
    String email,
    String password,
    String fullname
) {}