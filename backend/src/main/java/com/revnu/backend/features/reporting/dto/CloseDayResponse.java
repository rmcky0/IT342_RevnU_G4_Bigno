package com.revnu.backend.features.reporting.dto;

public record CloseDayResponse(
        DailySummaryDto summary,
        boolean reportSent
        ) {

}
