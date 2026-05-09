package com.revnu.backend.features.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.revnu.backend.features.auth.controller.AuthController;
import com.revnu.backend.features.auth.dto.AuthResponse;
import com.revnu.backend.features.auth.dto.LinkGoogleRequest;
import com.revnu.backend.features.auth.dto.LoginRequest;
import com.revnu.backend.features.auth.dto.RegisterRequest;
import com.revnu.backend.features.auth.model.AccountStatus;
import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private Authentication authentication;

    private AuthController createController() {
        return new AuthController(authService);
    }

    @Test
    @DisplayName("Register returns OK response from auth service")
    void register_returnsOkResponse() {
        RegisterRequest request = new RegisterRequest("tenant@revnu.com", "Tenant User", "password123");
        AuthResponse expected = new AuthResponse(
                "Registration successful",
                "tenant@revnu.com",
                "Tenant User",
                RoleType.TENANT,
                AccountStatus.ACTIVE,
                "local",
                false,
                "token-123");
        when(authService.register(request)).thenReturn(expected);

        ResponseEntity<?> response = createController().register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(authService).register(request);
    }

    @Test
    @DisplayName("Login returns unauthorized when auth service rejects credentials")
    void login_returnsUnauthorizedOnInvalidCredentials() {
        LoginRequest request = new LoginRequest("tenant@revnu.com", "wrong-password");
        when(authService.login(request)).thenThrow(new RuntimeException("Invalid email or password"));

        ResponseEntity<?> response = createController().login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
    }

    @Test
    @DisplayName("Link google returns bad request when validation fails")
    void linkGoogle_returnsBadRequestOnInvalidInput() {
        LinkGoogleRequest request = new LinkGoogleRequest("tenant@revnu.com", "wrong-password", "google-123");
        when(authService.linkGoogleAccount(request)).thenThrow(new IllegalArgumentException("Incorrect password"));

        ResponseEntity<?> response = createController().linkGoogle(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect password", response.getBody());
    }

    @Test
    @DisplayName("Logout forwards the Authorization header to auth service")
    void logout_forwardsAuthorizationHeader() {
        AuthResponse expected = new AuthResponse("Logout successful", null, null, null, null, null, false, null);
        when(authService.logout("Bearer abc.def.ghi")).thenReturn(expected);

        ResponseEntity<?> response = createController().logout("Bearer abc.def.ghi");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(authService).logout("Bearer abc.def.ghi");
    }

    @Test
    @DisplayName("Me returns current user response when authentication is present")
    void getCurrentUser_returnsCurrentUser() {
        AuthResponse expected = new AuthResponse(
                "Current user",
                "tenant@revnu.com",
                "Tenant User",
                RoleType.TENANT,
                AccountStatus.ACTIVE,
                "local",
                true,
                null);
        when(authentication.getName()).thenReturn("tenant@revnu.com");
        when(authService.getCurrentUser("tenant@revnu.com")).thenReturn(expected);

        ResponseEntity<?> response = createController().getCurrentUser(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(authService).getCurrentUser("tenant@revnu.com");
    }

    @Test
    @DisplayName("Me returns unauthorized when authentication is missing")
    void getCurrentUser_returnsUnauthorizedWhenAuthenticationIsMissing() {
        ResponseEntity<?> response = createController().getCurrentUser(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof String);
        verify(authService, never()).getCurrentUser(any());
    }
}
