package com.revnu.backend.features.settings.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.revnu.backend.features.settings.dto.ChangePasswordRequest;
import com.revnu.backend.features.settings.dto.RestaurantProfileRequest;

import com.revnu.backend.features.settings.dto.RestaurantSetupRequest;
import com.revnu.backend.features.settings.dto.UserProfileDto;
import com.revnu.backend.features.settings.service.SettingsService;
import com.revnu.backend.shared.exception.ApiResponse;
import com.revnu.backend.shared.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/revnu/settings")
@PreAuthorize("hasRole('RESTAURATEUR')")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    // ── User Profile 
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(Principal principal) {
        return ResponseEntity.ok(ResponseUtil.success(settingsService.getProfile(principal.getName())));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(
            Principal principal,
            @Valid @RequestBody UserProfileDto request) {
        return ResponseEntity.ok(ResponseUtil.success(settingsService.updateProfile(principal.getName(), request)));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        settingsService.changePassword(principal.getName(), request);
        return ResponseEntity.ok(ResponseUtil.success(Map.of("message", "Password updated successfully.")));
    }

    // ── Restaurant Profile
    @PostMapping("/restaurant")
    public ResponseEntity<ApiResponse> setupRestaurant(
            Principal principal,
            @Valid @RequestPart("data") RestaurantSetupRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logoFile) {
        return ResponseEntity.ok(ResponseUtil.success(settingsService.setupRestaurant(principal.getName(), request, logoFile)));
    }

    @GetMapping("/restaurant")
    public ResponseEntity<ApiResponse> getRestaurantProfile(Principal principal) {
        return ResponseEntity.ok(ResponseUtil.success(settingsService.getRestaurantProfile(principal.getName())));
    }

    @PutMapping("/restaurant")
    public ResponseEntity<ApiResponse> updateRestaurantProfile(
            Principal principal,
            @Valid @RequestBody RestaurantProfileRequest request) {
        return ResponseEntity.ok(ResponseUtil.success(settingsService.updateRestaurantProfile(principal.getName(), request)));
    }

    @PatchMapping("/restaurant/logo")
    public ResponseEntity<ApiResponse> updateRestaurantLogo(
            Principal principal,
            @RequestParam("logo") MultipartFile logo) {
        settingsService.updateRestaurantLogo(principal.getName(), logo);
        return ResponseEntity.ok(ResponseUtil.success(Map.of("message", "Logo updated successfully.")));
    }

}
