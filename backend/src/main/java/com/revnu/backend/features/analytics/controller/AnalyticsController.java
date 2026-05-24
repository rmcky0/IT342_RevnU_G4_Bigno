package com.revnu.backend.features.analytics.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.features.analytics.dto.DailyAnalyticsResponse;
import com.revnu.backend.features.analytics.dto.TrendAnalyticsResponse;
import com.revnu.backend.features.analytics.service.AnalyticsService;
import com.revnu.backend.shared.exception.ApiResponse;
import com.revnu.backend.shared.util.ResponseUtil;

@RestController
@RequestMapping("/revnu/analytics")
@PreAuthorize("hasRole('RESTAURATEUR')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse> getDailyAnalytics(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication
    ) {
        String email = authentication.getName();
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        DailyAnalyticsResponse response = analyticsService.getDailyAnalytics(email, targetDate);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @GetMapping("/trends")
    public ResponseEntity<ApiResponse> getTrends(
            @RequestParam(defaultValue = "daily") String period,
            Authentication authentication
    ) {
        String email = authentication.getName();
        TrendAnalyticsResponse response = analyticsService.getTrends(email, period);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getSummary(
            @RequestParam(defaultValue = "daily") String granularity,
            Authentication authentication
    ) {
        TrendAnalyticsResponse response = analyticsService.getTrends(authentication.getName(), granularity);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse> getRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        DailyAnalyticsResponse response = analyticsService.getDailyAnalytics(authentication.getName(), targetDate);
        return ResponseEntity.ok(ResponseUtil.success(Map.of(
                "date", targetDate.toString(),
                "totalSales", response.totalSales(),
                "saleRecordsCount", response.saleRecordsCount(),
                "salesTrend", response.salesTrend()
        )));
    }

    @GetMapping("/expenses")
    public ResponseEntity<ApiResponse> getExpenseAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        DailyAnalyticsResponse response = analyticsService.getDailyAnalytics(authentication.getName(), targetDate);
        return ResponseEntity.ok(ResponseUtil.success(Map.of(
                "date", targetDate.toString(),
                "totalExpenses", response.totalExpenses(),
                "expenseRecordsCount", response.expenseRecordsCount(),
                "categories", response.categories()
        )));
    }
}
