package com.revnu.backend.features.staff.controller;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.features.staff.dto.StaffProfileResponse;
import com.revnu.backend.features.staff.dto.StaffRequest;
import com.revnu.backend.features.staff.service.StaffService;
import com.revnu.backend.shared.exception.ApiResponse;
import com.revnu.backend.shared.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/revnu/staff")
@PreAuthorize("hasRole('RESTAURATEUR')")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addStaff(Principal principal, @Valid @RequestBody StaffRequest request) {
        return ResponseEntity.ok(ResponseUtil.success(staffService.addStaff(principal.getName(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getStaff(Principal principal) {
        return ResponseEntity.ok(ResponseUtil.success(staffService.getStaff(principal.getName())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getStaffById(Principal principal, @PathVariable UUID id) {
        StaffProfileResponse profile = staffService.getStaffById(principal.getName(), id);
        return ResponseEntity.ok(ResponseUtil.success(profile));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateStaff(
            Principal principal, @PathVariable UUID id, @Valid @RequestBody StaffRequest request) {
        return ResponseEntity.ok(ResponseUtil.success(staffService.updateStaff(principal.getName(), id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteStaff(Principal principal, @PathVariable UUID id) {
        staffService.deleteStaff(principal.getName(), id);
        return ResponseEntity.ok(ResponseUtil.success(Map.of("message", "Staff member deleted successfully")));
    }

}
