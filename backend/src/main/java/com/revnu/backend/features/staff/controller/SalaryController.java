package com.revnu.backend.features.staff.controller;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.features.staff.dto.SalaryRequest;
import com.revnu.backend.features.staff.dto.SalaryResponse;
import com.revnu.backend.features.staff.service.StaffService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/revnu/salaries")
@PreAuthorize("hasRole('TENANT')")
public class SalaryController {

    private final StaffService staffService;

    public SalaryController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> recordSalary(
            Principal principal,
            @Valid @RequestBody SalaryRequest request) {

        SalaryResponse response = staffService.recordSalaryPayout(principal.getName(), request);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSalaryHistory(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<SalaryResponse> pagedHistory = staffService.getSalaryHistory(principal.getName(), page, size);
        return ResponseEntity.ok(Map.of("success", true, "data", pagedHistory));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<Map<String, Object>> getStaffSalaries(
            Principal principal,
            @PathVariable UUID staffId) {

        return ResponseEntity.ok(Map.of("success", true, "data", staffService.getStaffSalaryHistory(principal.getName(), staffId)));
    }
}
