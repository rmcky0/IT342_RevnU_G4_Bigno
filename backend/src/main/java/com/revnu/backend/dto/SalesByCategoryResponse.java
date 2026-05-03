package com.revnu.backend.dto;

import java.math.BigDecimal;

public class SalesByCategoryResponse {
    private String category;
    private BigDecimal totalAmount;
    private int count;

    public SalesByCategoryResponse() {}

    public SalesByCategoryResponse(String category, BigDecimal totalAmount, int count) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.count = count;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
