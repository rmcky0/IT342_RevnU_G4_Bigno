package com.revnu.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.revnu.backend.dto.SalesRequest;
import com.revnu.backend.dto.SalesResponse;
import com.revnu.backend.model.Sales;
import com.revnu.backend.model.User;
import com.revnu.backend.repository.SalesRepository;
import com.revnu.backend.repository.UserRepository;

@Service
public class SalesService {
    private final SalesRepository salesRepository;
    private final UserRepository userRepository;

    public SalesService(SalesRepository salesRepository, UserRepository userRepository) {
        this.salesRepository = salesRepository;
        this.userRepository = userRepository;
    }

    public SalesResponse createSales(String userEmail, SalesRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Sales sales = new Sales(user, request.getSaleDate(), request.getAmount(), request.getDescription(), request.getPaymentMethod());
        Sales saved = salesRepository.save(sales);
        return toResponse(saved);
    }

    public List<SalesResponse> getSalesByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return salesRepository.findByUser(user).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<SalesResponse> getSalesByDateRange(String userEmail, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return salesRepository.findByUserAndSaleDateBetween(user, startDate, endDate).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public SalesResponse getSalesById(UUID id) {
        Sales sales = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales record not found"));
        return toResponse(sales);
    }

    public SalesResponse updateSales(UUID id, SalesRequest request) {
        Sales sales = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales record not found"));

        sales.setSaleDate(request.getSaleDate());
        sales.setAmount(request.getAmount());
        sales.setDescription(request.getDescription());
        sales.setPaymentMethod(request.getPaymentMethod());

        Sales updated = salesRepository.save(sales);
        return toResponse(updated);
    }

    public void deleteSales(UUID id) {
        Sales sales = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales record not found"));
        salesRepository.delete(sales);
    }

    private SalesResponse toResponse(Sales sales) {
        return new SalesResponse(sales.getId(), sales.getSaleDate(), sales.getAmount(), sales.getDescription(), sales.getPaymentMethod(), sales.getCreatedAt(), sales.getUpdatedAt());
    }
}
