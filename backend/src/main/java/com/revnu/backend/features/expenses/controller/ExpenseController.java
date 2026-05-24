package com.revnu.backend.features.expenses.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.multipart.MultipartFile;

import com.revnu.backend.features.expenses.dto.ExpenseRequest;
import com.revnu.backend.features.expenses.dto.ExpenseResponse;
import com.revnu.backend.features.expenses.service.ExpenseService;
import com.revnu.backend.shared.exception.ApiResponse;
import com.revnu.backend.shared.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/revnu/expenses")
@PreAuthorize("hasRole('RESTAURATEUR')")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> recordExpense(Principal principal, @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.recordExpense(principal.getName(), request);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateExpense(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.updateExpense(principal.getName(), id, request);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<ApiResponse> uploadReceipt(
            Principal principal,
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        ExpenseResponse response = expenseService.uploadReceipt(principal.getName(), id, file);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getExpenses(
            Principal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (date != null) {
            List<ExpenseResponse> expenses = expenseService.getExpensesByDate(principal.getName(), date);
            return ResponseEntity.ok(ResponseUtil.success(expenses));
        } else {
            Page<ExpenseResponse> expensesPage = expenseService.getAllExpenses(principal.getName(), page, size);
            return ResponseEntity.ok(ResponseUtil.success(expensesPage));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteExpense(
            Principal principal,
            @PathVariable UUID id) {
        expenseService.deleteExpense(principal.getName(), id);
        return ResponseEntity.ok(ResponseUtil.success(Map.of("message", "Expense deleted successfully")));
    }
}
