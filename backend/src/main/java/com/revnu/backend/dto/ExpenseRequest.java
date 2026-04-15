package com.revnu.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequest {
    private LocalDate expenseDate;
    private String category;
    private BigDecimal amount;
    private String description;
    private String paymentMethod;

    public ExpenseRequest() {}

    public ExpenseRequest(LocalDate expenseDate, String category, BigDecimal amount, String description, String paymentMethod) {
        this.expenseDate = expenseDate;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
