package com.revnu.backend.features.auth.dto;

public record LinkGoogleRequest(
    String email,
    String password,
    String googleId
) {}