package com.revnu.backend.features.admin.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.features.admin.dto.AdminRestaurantResponse;
import com.revnu.backend.features.admin.dto.AdminUserResponse;
import com.revnu.backend.features.admin.dto.UpdateUserRoleRequest;
import com.revnu.backend.features.admin.dto.UpdateUserStatusRequest;
import com.revnu.backend.features.admin.service.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/revnu/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ── Platform Stats ────────────────────────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of("success", true, "data", adminService.getPlatformStats()));
    }

    // ── User Management ───────────────────────────────────────────────────────
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AdminUserResponse> usersPage = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(Map.of("success", true, "data", usersPage));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        AdminUserResponse updated = adminService.updateUserStatus(id, request);
        return ResponseEntity.ok(Map.of("success", true, "data", updated));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Map<String, Object>> updateUserRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRoleRequest request) {

        AdminUserResponse updated = adminService.updateUserRole(id, request);
        return ResponseEntity.ok(Map.of("success", true, "data", updated));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "User deleted successfully."));
    }

    @GetMapping("/restaurants")
    public ResponseEntity<Map<String, Object>> getAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AdminRestaurantResponse> restaurantPage = adminService.getAllRestaurants(page, size);
        return ResponseEntity.ok(Map.of("success", true, "data", restaurantPage));
    }
}
