package com.revnu.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StaffRequest {
    private String staffName;
    private String position;
    private BigDecimal salary;
    private String paymentFrequency;
    private LocalDate hireDate;
    private String status;

    public StaffRequest() {}

    public StaffRequest(String staffName, String position, BigDecimal salary, String paymentFrequency, LocalDate hireDate, String status) {
        this.staffName = staffName;
        this.position = position;
        this.salary = salary;
        this.paymentFrequency = paymentFrequency;
        this.hireDate = hireDate;
        this.status = status;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
