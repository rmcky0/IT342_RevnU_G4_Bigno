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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/revnu/staff")
@PreAuthorize("hasRole('TENANT')")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addStaff(Principal principal, @Valid @RequestBody StaffRequest request) {
        return ResponseEntity.ok(Map.of("success", true, "data", staffService.addStaff(principal.getName(), request)));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStaff(Principal principal) {
        return ResponseEntity.ok(Map.of("success", true, "data", staffService.getStaff(principal.getName())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStaffById(Principal principal, @PathVariable UUID id) {
        StaffProfileResponse profile = staffService.getStaffById(principal.getName(), id);
        return ResponseEntity.ok(Map.of("success", true, "data", profile));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStaff(
            Principal principal, @PathVariable UUID id, @Valid @RequestBody StaffRequest request) {
        return ResponseEntity.ok(Map.of("success", true, "data", staffService.updateStaff(principal.getName(), id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStaff(Principal principal, @PathVariable UUID id) {
        staffService.deleteStaff(principal.getName(), id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Staff member deleted successfully"));
    }

}
