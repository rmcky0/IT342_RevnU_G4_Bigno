package com.revnu.backend.features.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.revnu.backend.features.auth.service.TokenBlacklistService;

@DisplayName("TokenBlacklistService Tests")
class TokenBlacklistServiceTest {

    @Test
    @DisplayName("Blacklist stores token and reports it as blacklisted")
    void blacklistToken_marksTokenAsBlacklisted() {
        TokenBlacklistService service = new TokenBlacklistService();

        service.blacklistToken("token-123");

        assertTrue(service.isTokenBlacklisted("token-123"));
        assertEquals(1, service.getBlacklistSize());
    }

    @Test
    @DisplayName("Clear blacklist removes all stored tokens")
    void clearBlacklist_removesAllTokens() {
        TokenBlacklistService service = new TokenBlacklistService();
        service.blacklistToken("token-123");
        service.blacklistToken("token-456");

        service.clearBlacklist();

        assertFalse(service.isTokenBlacklisted("token-123"));
        assertFalse(service.isTokenBlacklisted("token-456"));
        assertEquals(0, service.getBlacklistSize());
    }
}
