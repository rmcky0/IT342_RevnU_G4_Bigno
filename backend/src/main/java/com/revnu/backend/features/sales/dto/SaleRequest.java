package com.revnu.backend.features.sales.dto;

import java.math.BigDecimal;
import java.util.List;

public record SaleRequest(
        BigDecimal amount,
        List<String> tagNames,
        String description
        ) {

}
