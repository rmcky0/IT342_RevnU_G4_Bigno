package com.revnu.backend.features.external.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.features.external.dto.HolidayDto;
import com.revnu.backend.features.external.service.HolidayService;
import com.revnu.backend.shared.exception.ApiResponse;
import com.revnu.backend.shared.util.ResponseUtil;

@RestController
@RequestMapping("/revnu/external")
public class ExternalApiController {

    private final HolidayService holidayService;

    public ExternalApiController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping("/holidays")
    public ResponseEntity<ApiResponse> getHolidays(
            @RequestParam(required = false) Integer year) {

        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        List<HolidayDto> holidays = holidayService.getPhilippineHolidays(targetYear);

        return ResponseEntity.ok(ResponseUtil.success(holidays));
    }
}
