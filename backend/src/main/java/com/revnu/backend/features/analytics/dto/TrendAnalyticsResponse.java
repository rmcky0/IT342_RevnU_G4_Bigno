package com.revnu.backend.features.analytics.dto;

import java.util.List;

public record TrendAnalyticsResponse(
        String period,
        List<ProfitTrendPoint> points
        ) {

}
