package com.revnu.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.dto.StaffRequest;
import com.revnu.backend.dto.StaffResponse;
import com.revnu.backend.service.JwtService;
import com.revnu.backend.service.StaffService;

@RestController
@RequestMapping("/revnu/staff")
public class StaffController {
    private final StaffService staffService;
    private final JwtService jwtService;

    public StaffController(StaffService staffService, JwtService jwtService) {
        this.staffService = staffService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> addStaff(@RequestHeader("Authorization") String authHeader, @RequestBody StaffRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(token);
            StaffResponse result = staffService.createStaff(userEmail, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getStaff(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(token);
            List<StaffResponse> staff = staffService.getStaffByUser(userEmail);
            return ResponseEntity.ok(staff);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable UUID id) {
        try {
            StaffResponse staff = staffService.getStaffById(id);
            return ResponseEntity.ok(staff);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable UUID id, @RequestBody StaffRequest request) {
        try {
            StaffResponse result = staffService.updateStaff(id, request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable UUID id) {
        try {
            staffService.deleteStaff(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
