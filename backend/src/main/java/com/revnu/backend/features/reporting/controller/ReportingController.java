package com.revnu.backend.features.reporting.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.features.reporting.dto.CloseDayRequest;
import com.revnu.backend.features.reporting.dto.CloseDayResponse;
import com.revnu.backend.features.reporting.dto.DailySummaryDto;
import com.revnu.backend.features.reporting.dto.DayDetailResponse;
import com.revnu.backend.features.reporting.service.ReportingService;
import com.revnu.backend.shared.exception.ApiResponse;
import com.revnu.backend.shared.util.ResponseUtil;

@RestController
@RequestMapping("/revnu/day")
@PreAuthorize("hasRole('RESTAURATEUR')")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @PostMapping("/close")
    public ResponseEntity<ApiResponse> closeDay(Principal principal, @RequestBody CloseDayRequest request) {
        CloseDayResponse response = reportingService.closeDay(principal.getName(), request.date());
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @GetMapping("/summary/{date}")
    public ResponseEntity<ApiResponse> getSummary(
            Principal principal,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailySummaryDto summary = reportingService.getSummary(principal.getName(), date);
        return ResponseEntity.ok(ResponseUtil.success(summary));
    }

    @GetMapping("/summaries")
    public ResponseEntity<ApiResponse> getAllSummaries(Principal principal) {
        List<DailySummaryDto> summaries = reportingService.getAllSummaries(principal.getName());
        return ResponseEntity.ok(ResponseUtil.success(summaries));
    }

    @GetMapping("/detail/{date}")
    public ResponseEntity<ApiResponse> getDayDetail(
            Principal principal,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DayDetailResponse detail = reportingService.getDayDetail(principal.getName(), date);
        return ResponseEntity.ok(ResponseUtil.success(detail));
    }
}
