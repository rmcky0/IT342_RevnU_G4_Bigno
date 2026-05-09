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
import com.revnu.backend.features.settings.dto.RestaurantProfileResponse;
import com.revnu.backend.features.settings.dto.RestaurantSetupRequest;
import com.revnu.backend.features.settings.dto.SystemSettingsDto;
import com.revnu.backend.features.settings.dto.UserProfileDto;
import com.revnu.backend.features.settings.service.SettingsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/revnu/settings")
@PreAuthorize("hasRole('TENANT')")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    // ── User Profile 
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(Principal principal) {
        return ResponseEntity.ok(settingsService.getProfile(principal.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateProfile(
            Principal principal,
            @Valid @RequestBody UserProfileDto request) {
        return ResponseEntity.ok(settingsService.updateProfile(principal.getName(), request));
    }

    @PatchMapping("/profile/avatar")
    public ResponseEntity<UserProfileDto> updateProfileAvatar(
            Principal principal,
            @RequestParam("avatar") MultipartFile avatar) {
        return ResponseEntity.ok(settingsService.updateProfileAvatar(principal.getName(), avatar));
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        settingsService.changePassword(principal.getName(), request);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully."));
    }

    // ── Restaurant Profile
    @PostMapping("/restaurant")
    public ResponseEntity<RestaurantProfileResponse> setupRestaurant(
            Principal principal,
            @Valid @RequestPart("data") RestaurantSetupRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logoFile) {

        return ResponseEntity.ok(settingsService.setupRestaurant(principal.getName(), request, logoFile));
    }

    @GetMapping("/restaurant")
    public ResponseEntity<RestaurantProfileResponse> getRestaurantProfile(Principal principal) {
        return ResponseEntity.ok(settingsService.getRestaurantProfile(principal.getName()));
    }

    @PutMapping("/restaurant")
    public ResponseEntity<RestaurantProfileResponse> updateRestaurantProfile(
            Principal principal,
            @Valid @RequestBody RestaurantProfileRequest request) {
        return ResponseEntity.ok(settingsService.updateRestaurantProfile(principal.getName(), request));
    }

    @PatchMapping("/restaurant/logo")
    public ResponseEntity<Map<String, String>> updateRestaurantLogo(
            Principal principal,
            @RequestParam("logo") MultipartFile logo) {
        settingsService.updateRestaurantLogo(principal.getName(), logo);
        return ResponseEntity.ok(Map.of("message", "Logo updated successfully."));
    }

    @GetMapping("/system")
    public ResponseEntity<SystemSettingsDto> getSystemSettings(Principal principal) {
        return ResponseEntity.ok(settingsService.getSystemSettings(principal.getName()));
    }

    @PutMapping("/system")
    public ResponseEntity<SystemSettingsDto> updateSystemSettings(
            Principal principal,
            @Valid @RequestBody SystemSettingsDto request) {
        return ResponseEntity.ok(settingsService.updateSystemSettings(principal.getName(), request));
    }
}
