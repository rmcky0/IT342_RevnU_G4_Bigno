package com.revnu.backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.dto.SalesRequest;
import com.revnu.backend.dto.SalesResponse;
import com.revnu.backend.service.JwtService;
import com.revnu.backend.service.SalesService;

@RestController
@RequestMapping("/revnu/sales")
public class SalesController {
    private final SalesService salesService;
    private final JwtService jwtService;

    public SalesController(SalesService salesService, JwtService jwtService) {
        this.salesService = salesService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> addSale(@RequestHeader("Authorization") String authHeader, @RequestBody SalesRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(token);
            SalesResponse result = salesService.createSales(userEmail, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getSales(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(token);
            List<SalesResponse> sales = salesService.getSalesByUser(userEmail);
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/range")
    public ResponseEntity<?> getSalesByDateRange(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(token);
            List<SalesResponse> sales = salesService.getSalesByDateRange(userEmail, startDate, endDate);
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSaleById(@PathVariable UUID id) {
        try {
            SalesResponse sale = salesService.getSalesById(id);
            return ResponseEntity.ok(sale);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSale(@PathVariable UUID id, @RequestBody SalesRequest request) {
        try {
            SalesResponse result = salesService.updateSales(id, request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSale(@PathVariable UUID id) {
        try {
            salesService.deleteSales(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
