package com.revnu.backend.features.reporting.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

@RestController
@RequestMapping("/revnu/day")
@PreAuthorize("hasRole('TENANT')")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @PostMapping("/close")
    public ResponseEntity<Map<String, Object>> closeDay(Principal principal, @RequestBody CloseDayRequest request) {
        CloseDayResponse response = reportingService.closeDay(principal.getName(), request.date());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    /**
     * GET /revnu/day/summary/{date} — single EOD summary
     */
    @GetMapping("/summary/{date}")
    public ResponseEntity<Map<String, Object>> getSummary(
            Principal principal,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailySummaryDto summary = reportingService.getSummary(principal.getName(), date);
        return ResponseEntity.ok(Map.of("success", true, "data", summary));
    }

    /**
     * GET /revnu/day/summaries Returns all closed days for the tenant, newest
     * first. Feeds the "Archived Records" list on the frontend.
     */
    @GetMapping("/summaries")
    public ResponseEntity<Map<String, Object>> getAllSummaries(Principal principal) {
        List<DailySummaryDto> summaries = reportingService.getAllSummaries(principal.getName());
        return ResponseEntity.ok(Map.of("success", true, "data", summaries));
    }

    /**
     * GET /revnu/day/detail/{date} Returns the full EOD detail for one date:
     * summary totals + every individual sale + expense record from that day.
     * Feeds the "Archived Detail" page on the frontend.
     */
    @GetMapping("/detail/{date}")
    public ResponseEntity<Map<String, Object>> getDayDetail(
            Principal principal,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DayDetailResponse detail = reportingService.getDayDetail(principal.getName(), date);
        return ResponseEntity.ok(Map.of("success", true, "data", detail));
    }
}
