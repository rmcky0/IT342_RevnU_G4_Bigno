package com.revnu.backend.features.reporting.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.expenses.dto.ExpenseResponse;
import com.revnu.backend.features.expenses.model.Expense;
import com.revnu.backend.features.expenses.model.ExpenseStatus;
import com.revnu.backend.features.expenses.repository.ExpenseRepository;
import com.revnu.backend.features.notifications.service.NotificationService;
import com.revnu.backend.features.reporting.dto.CashFlowRecord;
import com.revnu.backend.features.reporting.dto.CloseDayResponse;
import com.revnu.backend.features.reporting.dto.DailySummaryDto;
import com.revnu.backend.features.reporting.dto.DayDetailResponse;
import com.revnu.backend.features.reporting.model.DailySummary;
import com.revnu.backend.features.reporting.repository.DailySummaryRepository;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.sales.dto.SaleResponse;
import com.revnu.backend.features.sales.model.Sale;
import com.revnu.backend.features.sales.model.SaleStatus;
import com.revnu.backend.features.sales.repository.SaleRepository;
import com.revnu.backend.features.staff.model.Salary;
import com.revnu.backend.features.staff.model.SalaryStatus;
import com.revnu.backend.features.staff.repository.SalaryRepository;
import com.revnu.backend.features.tags.model.Tag;

@Service
public class ReportingService {

    private final DailySummaryRepository summaryRepository;
    private final SaleRepository saleRepository;
    private final ExpenseRepository expenseRepository;
    private final SalaryRepository salaryRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReportingNotificationService notificationService;
    private final NotificationService inAppNotificationService;

    public ReportingService(
            DailySummaryRepository summaryRepository,
            SaleRepository saleRepository,
            ExpenseRepository expenseRepository,
            SalaryRepository salaryRepository,
            UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            ReportingNotificationService notificationService,
            NotificationService inAppNotificationService
    ) {
        this.summaryRepository = summaryRepository;
        this.saleRepository = saleRepository;
        this.expenseRepository = expenseRepository;
        this.salaryRepository = salaryRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.notificationService = notificationService;
        this.inAppNotificationService = inAppNotificationService;
    }

    // ── EOD Close ─────────────────────────────────────────────────────────────
    @Transactional
    public CloseDayResponse closeDay(String ownerEmail, LocalDate date) {
        User owner = getUser(ownerEmail);
        Restaurant restaurant = getRestaurant(owner);

        if (summaryRepository.existsByRestaurantAndReportDate(restaurant, date)) {
            throw new IllegalStateException("Records for " + date + " are already closed.");
        }

        List<Sale> sales = saleRepository.findByRestaurantAndCreatedAtBetweenAndStatus(
                restaurant, date.atStartOfDay(), date.atTime(23, 59, 59), SaleStatus.OPEN);

        List<Expense> expenses = expenseRepository.findByRestaurantAndCreatedAtBetweenAndStatus(
                restaurant, date.atStartOfDay(), date.atTime(23, 59, 59), ExpenseStatus.OPEN);

        List<Salary> salaries = salaryRepository.findByRestaurantAndPaymentDateAndStatus(
                restaurant, date, SalaryStatus.OPEN);

        BigDecimal totalSales = sum(sales.stream().map(Sale::getAmount).toList());
        BigDecimal totalExpenses = sum(expenses.stream().map(Expense::getAmount).toList());
        BigDecimal totalSalaries = sum(salaries.stream().map(Salary::getAmount).toList());
        BigDecimal netProfit = totalSales.subtract(totalExpenses).subtract(totalSalaries);

        sales.forEach(s -> s.setStatus(SaleStatus.CLOSED));
        saleRepository.saveAll(sales);

        expenses.forEach(e -> e.setStatus(ExpenseStatus.CLOSED));
        expenseRepository.saveAll(expenses);

        salaries.forEach(s -> s.setStatus(SalaryStatus.CLOSED));
        salaryRepository.saveAll(salaries);

        DailySummary summary = DailySummary.builder()
                .reportDate(date)
                .totalSales(totalSales)
                .totalExpenses(totalExpenses)
                .totalSalaries(totalSalaries)
                .netProfit(netProfit)
                .restaurant(restaurant)
                .reportSent(true)
                .build();

        summaryRepository.save(summary);

        inAppNotificationService.createEodCloseNotification(owner, restaurant, summary);

        notificationService.sendEodSummaryEmail(owner, restaurant, summary);

        return new CloseDayResponse(toDto(summary), true);
    }

    @Transactional(readOnly = true)
    public DailySummaryDto getSummary(String ownerEmail, LocalDate date) {
        User owner = getUser(ownerEmail);
        Restaurant restaurant = getRestaurant(owner);

        DailySummary summary = summaryRepository
                .findByRestaurantAndReportDate(restaurant, date)
                .orElseThrow(() -> new IllegalArgumentException(
                "No EOD summary found for " + date + ". Has this day been closed yet?"));

        return toDto(summary);
    }

    @Transactional(readOnly = true)
    public List<DailySummaryDto> getAllSummaries(String ownerEmail) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        return summaryRepository.findByRestaurantOrderByReportDateDesc(restaurant)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DayDetailResponse getDayDetail(String ownerEmail, LocalDate date) {
        User owner = getUser(ownerEmail);
        Restaurant restaurant = getRestaurant(owner);

        DailySummary summary = summaryRepository
                .findByRestaurantAndReportDate(restaurant, date)
                .orElseThrow(() -> new IllegalArgumentException(
                "No EOD summary found for " + date));

        List<SaleResponse> sales = saleRepository
                .findByRestaurantAndCreatedAtBetween(
                        restaurant, date.atStartOfDay(), date.atTime(23, 59, 59))
                .stream().map(this::mapSale).collect(Collectors.toList());

        List<ExpenseResponse> expenses = expenseRepository
                .findByRestaurantAndCreatedAtBetween(
                        restaurant, date.atStartOfDay(), date.atTime(23, 59, 59))
                .stream().map(this::mapExpense).collect(Collectors.toList());

        List<CashFlowRecord> outflow = new java.util.ArrayList<>();
        expenseRepository
                .findByRestaurantAndCreatedAtBetween(
                        restaurant, date.atStartOfDay(), date.atTime(23, 59, 59))
                .forEach(e -> outflow.add(new CashFlowRecord(
                e.getId(), "EXPENSE", e.getAmount(), e.getDescription(), e.getCreatedAt())));
        salaryRepository
                .findByRestaurantAndPaymentDate(restaurant, date)
                .forEach(s -> outflow.add(new CashFlowRecord(
                s.getId(), "PAYROLL", s.getAmount(),
                "Payroll: " + s.getStaff().getFullname(), s.getCreatedAt())));
        outflow.sort(java.util.Comparator.comparing(CashFlowRecord::timestamp,
                java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())));

        return new DayDetailResponse(toDto(summary), sales, expenses, outflow);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private Restaurant getRestaurant(User owner) {
        return restaurantRepository.findByOwner(owner)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found."));
    }

    private BigDecimal sum(List<BigDecimal> amounts) {
        return amounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private DailySummaryDto toDto(DailySummary s) {
        return new DailySummaryDto(
                s.getReportDate(),
                s.getTotalSales(),
                s.getTotalExpenses(),
                s.getTotalSalaries(),
                s.getNetProfit(),
                s.isReportSent()
        );
    }

    private SaleResponse mapSale(Sale s) {
        List<String> tagNames = s.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        return new SaleResponse(s.getId(), s.getAmount(), tagNames, s.getDescription(),
                s.getStatus(), s.getCreatedAt());
    }

    private ExpenseResponse mapExpense(Expense e) {
        List<String> tagNames = e.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        return new ExpenseResponse(e.getId(), e.getAmount(), tagNames, e.getDescription(),
                e.getFile() != null ? e.getFile().getId() : null, e.getStatus(), e.getCreatedAt());
    }
}
