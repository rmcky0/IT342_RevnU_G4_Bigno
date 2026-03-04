package com.revnu.backend.model;

public enum RoleType {
    OWNER,   // The first user; has full access and EOD control
    MANAGER, // Can record transactions and view staff logs
    STAFF    // Restricted to recording sales and expenses only
}