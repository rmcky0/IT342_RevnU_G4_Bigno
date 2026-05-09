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

@RestController
@RequestMapping("/revnu/analytics")
@PreAuthorize("hasRole('TENANT')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> getDailyAnalytics(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication
    ) {
        String email = authentication.getName();
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        DailyAnalyticsResponse response = analyticsService.getDailyAnalytics(email, targetDate);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getTrends(
            @RequestParam(defaultValue = "daily") String period,
            Authentication authentication
    ) {
        String email = authentication.getName();
        TrendAnalyticsResponse response = analyticsService.getTrends(email, period);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }
}
