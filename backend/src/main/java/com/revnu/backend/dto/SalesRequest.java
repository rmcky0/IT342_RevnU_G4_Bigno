package com.revnu.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class SalesRequest {
    private LocalDate saleDate;
    private BigDecimal amount;
    private String description;
    private String paymentMethod;

    public SalesRequest() {}

    public SalesRequest(LocalDate saleDate, BigDecimal amount, String description, String paymentMethod) {
        this.saleDate = saleDate;
        this.amount = amount;
        this.description = description;
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
