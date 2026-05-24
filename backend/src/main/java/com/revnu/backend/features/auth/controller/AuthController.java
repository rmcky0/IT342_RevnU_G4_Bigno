package com.revnu.backend.features.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.features.auth.dto.AuthResponse;
import com.revnu.backend.features.auth.dto.ForgotPasswordRequest;
import com.revnu.backend.features.auth.dto.GoogleAuthRequest;
import com.revnu.backend.features.auth.dto.LinkGoogleRequest;
import com.revnu.backend.features.auth.dto.LoginRequest;
import com.revnu.backend.features.auth.dto.RefreshTokenRequest;
import com.revnu.backend.features.auth.dto.RegisterRequest;
import com.revnu.backend.features.auth.dto.ResetPasswordRequest;
import com.revnu.backend.features.auth.dto.VerifyOtpRequest;
import com.revnu.backend.features.auth.service.AuthService;
import com.revnu.backend.features.auth.service.PasswordResetService;
import com.revnu.backend.shared.exception.ApiResponse;
import com.revnu.backend.shared.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/revnu/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse> googleLogin(@Valid @RequestBody GoogleAuthRequest request) {
        AuthResponse response = authService.authenticateWithGoogleToken(request.idToken());
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @PostMapping("/link-google")
    public ResponseEntity<ApiResponse> linkGoogle(@RequestBody LinkGoogleRequest request) {
        AuthResponse response = authService.linkGoogleAccount(request);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        AuthResponse response = authService.logout(token);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        AuthResponse response = authService.getCurrentUser(email);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestOtp(request.email());
        return ResponseEntity.ok(ResponseUtil.success(Map.of("message", "OTP sent to your email.")));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        passwordResetService.verifyOtp(request.email(), request.otp());
        return ResponseEntity.ok(ResponseUtil.success(Map.of("message", "OTP verified.")));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.email(), request.otp(), request.newPassword());
        return ResponseEntity.ok(ResponseUtil.success(Map.of("message", "Password reset successfully.")));
    }
}
