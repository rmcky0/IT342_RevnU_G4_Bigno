package com.revnu.backend.model;

public enum UserStatus {
    PENDING,   // Default for new staff; waiting for Owner approval
    ACTIVE,    // Account is approved and can log into the system
    INACTIVE   // Account is disabled by the Owner (e.g., staff resigned)
}