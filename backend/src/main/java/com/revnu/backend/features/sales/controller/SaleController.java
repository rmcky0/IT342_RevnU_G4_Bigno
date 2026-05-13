package com.revnu.backend.features.sales.controller;

import java.security.Principal;
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

@RestController
@RequestMapping("/revnu/sales")
@PreAuthorize("hasRole('TENANT')")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> recordSale(
            Authentication authentication,
            @RequestBody SaleRequest request) {
        String email = authentication.getName();
        SaleResponse response = saleService.recordSale(email, request);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSales(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<SaleResponse> pagedSales = saleService.getAllSales(principal.getName(), page, size);

        return ResponseEntity.ok(Map.of("success", true, "data", pagedSales));
    }

    @GetMapping("/date")
    public ResponseEntity<Map<String, Object>> getSalesByDate(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String email = authentication.getName();
        return ResponseEntity.ok(Map.of("success", true, "data", saleService.getSalesByDate(email, date)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSale(
            Authentication authentication,
            @PathVariable UUID id,
            @RequestBody SaleRequest request) {
        String email = authentication.getName();
        SaleResponse response = saleService.updateSale(email, id, request);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSale(
            Authentication authentication,
            @PathVariable UUID id) {
        String email = authentication.getName();
        saleService.deleteSale(email, id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Sale deleted successfully"));
    }
}
