package com.revnu.backend.dto;

import java.math.BigDecimal;

public class SalesTrendResponse {
    private String time;
    private BigDecimal amount;
    private String description;

    public SalesTrendResponse() {}

    public SalesTrendResponse(String time, BigDecimal amount, String description) {
        this.time = time;
        this.amount = amount;
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
