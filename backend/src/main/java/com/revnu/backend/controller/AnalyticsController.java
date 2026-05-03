package com.revnu.backend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.dto.AnalyticsSummaryResponse;
import com.revnu.backend.dto.SalesByCategoryResponse;
import com.revnu.backend.dto.SalesResponse;
import com.revnu.backend.dto.ExpenseResponse;
import com.revnu.backend.service.JwtService;
import com.revnu.backend.service.SalesService;
import com.revnu.backend.service.ExpenseService;

@RestController
@RequestMapping("/revnu/analytics")
public class AnalyticsController {
    private final SalesService salesService;
    private final ExpenseService expenseService;
    private final JwtService jwtService;

    public AnalyticsController(SalesService salesService, ExpenseService expenseService, JwtService jwtService) {
        this.salesService = salesService;
        this.expenseService = expenseService;
        this.jwtService = jwtService;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getDailySummary(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(token);

            // Get sales for the date
            List<SalesResponse> sales = salesService.getSalesByDateRange(userEmail, date, date);
            BigDecimal totalSales = sales.stream()
                    .map(SalesResponse::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Get expenses for the date
            List<ExpenseResponse> expenses = expenseService.getExpensesByDateRange(userEmail, date, date);
            BigDecimal totalExpenses = expenses.stream()
                    .map(ExpenseResponse::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calculate net profit
            BigDecimal netProfit = totalSales.subtract(totalExpenses);

            return ResponseEntity.ok(new AnalyticsSummaryResponse(totalSales, totalExpenses, netProfit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching summary: " + e.getMessage());
        }
    }

    @GetMapping("/sales-trend")
    public ResponseEntity<?> getSalesTrend(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(token);

            List<SalesResponse> sales = salesService.getSalesByDateRange(userEmail, startDate, endDate);
            
            // Group sales by hour or time slot
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching sales trend: " + e.getMessage());
        }
    }

    @GetMapping("/sales-by-category")
    public ResponseEntity<?> getSalesByCategory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(token);

            List<SalesResponse> sales = salesService.getSalesByDateRange(userEmail, date, date);
            
            // Group sales by description (category)
            var categorySales = sales.stream()
                    .collect(Collectors.groupingBy(
                            SalesResponse::getDescription,
                            Collectors.reducing(
                                    BigDecimal.ZERO,
                                    SalesResponse::getAmount,
                                    BigDecimal::add
                            )
                    ))
                    .entrySet().stream()
                    .map(entry -> new SalesByCategoryResponse(
                            entry.getKey() != null ? entry.getKey() : "Other",
                            entry.getValue(),
                            1
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(categorySales);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching sales by category: " + e.getMessage());
        }
    }

    @GetMapping("/sales-summary")
    public ResponseEntity<?> getSalesSummary(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(token);

            List<SalesResponse> sales = salesService.getSalesByDateRange(userEmail, startDate, endDate);
            
            // Return aggregated sales data
            BigDecimal totalSales = sales.stream()
                    .map(SalesResponse::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<ExpenseResponse> expenses = expenseService.getExpensesByDateRange(userEmail, startDate, endDate);
            BigDecimal totalExpenses = expenses.stream()
                    .map(ExpenseResponse::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<Object> summary = new ArrayList<>();
            summary.add(new java.util.HashMap<String, Object>() {{
                put("time", "12:00");
                put("sales", totalSales);
                put("profit", totalSales.subtract(totalExpenses));
            }});

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching sales summary: " + e.getMessage());
        }
    }

    @GetMapping("/profit-trend")
    public ResponseEntity<?> getProfitTrend(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(token);

            List<SalesResponse> sales = salesService.getSalesByDateRange(userEmail, startDate, endDate);
            BigDecimal totalSales = sales.stream()
                    .map(SalesResponse::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<ExpenseResponse> expenses = expenseService.getExpensesByDateRange(userEmail, startDate, endDate);
            BigDecimal totalExpenses = expenses.stream()
                    .map(ExpenseResponse::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal profit = totalSales.subtract(totalExpenses);

            List<Object> trendData = new ArrayList<>();
            trendData.add(new java.util.HashMap<String, Object>() {{
                put("time", "12:00");
                put("today", profit);
                put("yesterday", profit.multiply(new BigDecimal("0.9"))); // Mock yesterday's data
            }});

            return ResponseEntity.ok(trendData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching profit trend: " + e.getMessage());
        }
    }
}
