package com.revnu.backend.features.staff.controller;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.features.staff.dto.SalaryRequest;
import com.revnu.backend.features.staff.dto.SalaryResponse;
import com.revnu.backend.features.staff.service.StaffService;
import com.revnu.backend.shared.exception.ApiResponse;
import com.revnu.backend.shared.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/revnu/salaries")
@PreAuthorize("hasRole('RESTAURATEUR')")
public class SalaryController {

    private final StaffService staffService;

    public SalaryController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> recordSalary(
            Principal principal,
            @Valid @RequestBody SalaryRequest request) {

        SalaryResponse response = staffService.recordSalaryPayout(principal.getName(), request);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getSalaryHistory(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<SalaryResponse> pagedHistory = staffService.getSalaryHistory(principal.getName(), page, size);
        return ResponseEntity.ok(ResponseUtil.success(pagedHistory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSalary(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody SalaryRequest request) {

        SalaryResponse response = staffService.updateSalaryPayout(principal.getName(), id, request);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSalary(
            Principal principal,
            @PathVariable UUID id) {

        staffService.deleteSalaryPayout(principal.getName(), id);
        return ResponseEntity.ok(ResponseUtil.success(Map.of("message", "Salary record deleted successfully")));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<ApiResponse> getStaffSalaries(
            Principal principal,
            @PathVariable UUID staffId) {

        return ResponseEntity.ok(ResponseUtil.success(staffService.getStaffSalaryHistory(principal.getName(), staffId)));
    }
}
