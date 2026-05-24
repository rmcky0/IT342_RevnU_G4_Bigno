package com.revnu.backend.features.sales.controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.features.sales.dto.SaleRequest;
import com.revnu.backend.features.sales.dto.SaleResponse;
import com.revnu.backend.features.sales.service.SaleService;
import com.revnu.backend.shared.exception.ApiResponse;
import com.revnu.backend.shared.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/revnu/sales")
@PreAuthorize("hasRole('RESTAURATEUR')")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> recordSale(
            Authentication authentication,
            @Valid @RequestBody SaleRequest request) {
        String email = authentication.getName();
        SaleResponse response = saleService.recordSale(email, request);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllSales(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String email = authentication.getName();

        if (date != null) {
            return ResponseEntity.ok(ResponseUtil.success(saleService.getSalesByDate(email, date)));
        }

        Page<SaleResponse> pagedSales = saleService.getAllSales(email, page, size);
        return ResponseEntity.ok(ResponseUtil.success(pagedSales));
    }

    @GetMapping("/date")
    public ResponseEntity<ApiResponse> getSalesByDate(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String email = authentication.getName();
        return ResponseEntity.ok(ResponseUtil.success(saleService.getSalesByDate(email, date)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSale(
            Authentication authentication,
            @PathVariable UUID id,
            @Valid @RequestBody SaleRequest request) {
        String email = authentication.getName();
        SaleResponse response = saleService.updateSale(email, id, request);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSale(
            Authentication authentication,
            @PathVariable UUID id) {
        String email = authentication.getName();
        saleService.deleteSale(email, id);
        return ResponseEntity.ok(ResponseUtil.success(Map.of("message", "Sale deleted successfully")));
    }
}
