package com.revnu.backend.features.analytics.dto;

import java.math.BigDecimal;

public record HourlySalePoint(
        Integer hour,
        BigDecimal total
        ) {

}
