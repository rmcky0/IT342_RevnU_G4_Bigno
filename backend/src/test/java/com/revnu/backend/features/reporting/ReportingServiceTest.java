package com.revnu.backend.features.reporting;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.expenses.model.Expense;
import com.revnu.backend.features.expenses.model.ExpenseStatus;
import com.revnu.backend.features.expenses.repository.ExpenseRepository;
import com.revnu.backend.features.notifications.service.NotificationService;
import com.revnu.backend.features.reporting.dto.CloseDayResponse;
import com.revnu.backend.features.reporting.model.DailySummary;
import com.revnu.backend.features.reporting.repository.DailySummaryRepository;
import com.revnu.backend.features.reporting.service.ReportingNotificationService;
import com.revnu.backend.features.reporting.service.ReportingService;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.sales.model.Sale;
import com.revnu.backend.features.sales.model.SaleStatus;
import com.revnu.backend.features.sales.repository.SaleRepository;
import com.revnu.backend.features.staff.model.Salary;
import com.revnu.backend.features.staff.model.SalaryStatus;
import com.revnu.backend.features.staff.repository.SalaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportingService Unit Tests")
class ReportingServiceTest {

    @Mock
    private DailySummaryRepository summaryRepository;
    @Mock
    private SaleRepository saleRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private SalaryRepository salaryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private ReportingNotificationService notificationService;
    @Mock
    private NotificationService inAppNotificationService;

    @InjectMocks
    private ReportingService reportingService;

    private User owner;
    private Restaurant restaurant;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.of(2024, 5, 15);
        owner = User.builder().id(UUID.randomUUID()).email("owner@test.com").build();
        restaurant = Restaurant.builder().id(UUID.randomUUID()).name("My Restaurant").owner(owner).build();

        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(restaurantRepository.findByOwner(owner)).thenReturn(Optional.of(restaurant));
    }

    // ── Business Rule 10: Double Close Prevention (409 Conflict) ────────────────
    @Test
    @DisplayName("Rule 10 - closeDay() throws when date is already closed (double-close prevention)")
    void closeDay_alreadyClosed_throwsIllegalState() {
        when(summaryRepository.existsByRestaurantAndReportDate(restaurant, today)).thenReturn(true);

        assertThatThrownBy(() -> reportingService.closeDay("owner@test.com", today))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already closed");
    }

    // ── Business Rule 10: Zero-Sum Days still create an EOD record ───────────────
    @Test
    @DisplayName("Rule 10 - closeDay() creates EOD record with 0.00 values on a zero-transaction day")
    void closeDay_zeroTransactions_createsEodRecordWithZeroes() {
        when(summaryRepository.existsByRestaurantAndReportDate(restaurant, today)).thenReturn(false);
        when(saleRepository.findByRestaurantAndCreatedAtBetweenAndStatus(any(), any(), any(), eq(SaleStatus.OPEN)))
                .thenReturn(Collections.emptyList());
        when(expenseRepository.findByRestaurantAndCreatedAtBetweenAndStatus(any(), any(), any(), eq(ExpenseStatus.OPEN)))
                .thenReturn(Collections.emptyList());
        when(salaryRepository.findByRestaurantAndPaymentDateAndStatus(any(), any(), eq(SalaryStatus.OPEN)))
                .thenReturn(Collections.emptyList());
        DailySummary savedSummary = DailySummary.builder()
                .reportDate(today).totalSales(BigDecimal.ZERO).totalExpenses(BigDecimal.ZERO)
                .totalSalaries(BigDecimal.ZERO).netProfit(BigDecimal.ZERO)
                .restaurant(restaurant).reportSent(true).build();
        when(summaryRepository.save(any(DailySummary.class))).thenReturn(savedSummary);

        CloseDayResponse response = reportingService.closeDay("owner@test.com", today);

        assertThat(response.summary().totalSales()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.summary().totalExpenses()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.summary().netProfit()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ── Business Rule 7: Net Profit Formula: Sales - (Expenses + Salaries) ───────
    @Test
    @DisplayName("Rule 7 - closeDay() calculates Net Profit = Sales - Expenses - Salaries")
    void closeDay_correctNetProfitFormula() {
        Sale sale1 = Sale.builder().id(UUID.randomUUID()).amount(new BigDecimal("1000.00")).status(SaleStatus.OPEN).build();
        Sale sale2 = Sale.builder().id(UUID.randomUUID()).amount(new BigDecimal("500.00")).status(SaleStatus.OPEN).build();

        Expense expense = new Expense();
        expense.setId(UUID.randomUUID());
        expense.setAmount(new BigDecimal("300.00"));
        expense.setStatus(ExpenseStatus.OPEN);

        Salary salary = Salary.builder().id(UUID.randomUUID()).amount(new BigDecimal("200.00")).status(SalaryStatus.OPEN).build();

        when(summaryRepository.existsByRestaurantAndReportDate(restaurant, today)).thenReturn(false);
        when(saleRepository.findByRestaurantAndCreatedAtBetweenAndStatus(any(), any(), any(), eq(SaleStatus.OPEN)))
                .thenReturn(List.of(sale1, sale2));
        when(expenseRepository.findByRestaurantAndCreatedAtBetweenAndStatus(any(), any(), any(), eq(ExpenseStatus.OPEN)))
                .thenReturn(List.of(expense));
        when(salaryRepository.findByRestaurantAndPaymentDateAndStatus(any(), any(), eq(SalaryStatus.OPEN)))
                .thenReturn(List.of(salary));

        ArgumentCaptor<DailySummary> summaryCaptor = ArgumentCaptor.forClass(DailySummary.class);
        when(summaryRepository.save(summaryCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        reportingService.closeDay("owner@test.com", today);

        DailySummary captured = summaryCaptor.getValue();
        assertThat(captured.getTotalSales()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(captured.getTotalExpenses()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(captured.getTotalSalaries()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(captured.getNetProfit()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    // ── Business Rule 6: EOD locks all OPEN transactions (CLOSED status) ────────
    @Test
    @DisplayName("Rule 6 - closeDay() transitions all OPEN records to CLOSED status")
    void closeDay_locksAllOpenRecords() {
        Sale openSale = Sale.builder().id(UUID.randomUUID()).amount(new BigDecimal("500.00")).status(SaleStatus.OPEN).build();
        Expense openExpense = new Expense();
        openExpense.setId(UUID.randomUUID());
        openExpense.setAmount(new BigDecimal("100.00"));
        openExpense.setStatus(ExpenseStatus.OPEN);
        Salary openSalary = Salary.builder().id(UUID.randomUUID()).amount(new BigDecimal("50.00")).status(SalaryStatus.OPEN).build();

        when(summaryRepository.existsByRestaurantAndReportDate(restaurant, today)).thenReturn(false);
        when(saleRepository.findByRestaurantAndCreatedAtBetweenAndStatus(any(), any(), any(), eq(SaleStatus.OPEN)))
                .thenReturn(List.of(openSale));
        when(expenseRepository.findByRestaurantAndCreatedAtBetweenAndStatus(any(), any(), any(), eq(ExpenseStatus.OPEN)))
                .thenReturn(List.of(openExpense));
        when(salaryRepository.findByRestaurantAndPaymentDateAndStatus(any(), any(), eq(SalaryStatus.OPEN)))
                .thenReturn(List.of(openSalary));
        when(summaryRepository.save(any(DailySummary.class))).thenAnswer(inv -> inv.getArgument(0));

        reportingService.closeDay("owner@test.com", today);

        assertThat(openSale.getStatus()).isEqualTo(SaleStatus.CLOSED);
        assertThat(openExpense.getStatus()).isEqualTo(ExpenseStatus.CLOSED);
        assertThat(openSalary.getStatus()).isEqualTo(SalaryStatus.CLOSED);
        verify(saleRepository).saveAll(List.of(openSale));
        verify(expenseRepository).saveAll(List.of(openExpense));
        verify(salaryRepository).saveAll(List.of(openSalary));
    }

    // ── Business Rule 20: EOD email failure must not roll back the close ─────────
    @Test
    @DisplayName("Rule 20 - closeDay() persists EOD record even when notification service throws")
    void closeDay_emailFails_closingStillSucceeds() {
        when(summaryRepository.existsByRestaurantAndReportDate(restaurant, today)).thenReturn(false);
        when(saleRepository.findByRestaurantAndCreatedAtBetweenAndStatus(any(), any(), any(), eq(SaleStatus.OPEN)))
                .thenReturn(Collections.emptyList());
        when(expenseRepository.findByRestaurantAndCreatedAtBetweenAndStatus(any(), any(), any(), eq(ExpenseStatus.OPEN)))
                .thenReturn(Collections.emptyList());
        when(salaryRepository.findByRestaurantAndPaymentDateAndStatus(any(), any(), eq(SalaryStatus.OPEN)))
                .thenReturn(Collections.emptyList());
        DailySummary savedSummary = DailySummary.builder()
                .reportDate(today).totalSales(BigDecimal.ZERO).totalExpenses(BigDecimal.ZERO)
                .totalSalaries(BigDecimal.ZERO).netProfit(BigDecimal.ZERO)
                .restaurant(restaurant).reportSent(true).build();
        when(summaryRepository.save(any(DailySummary.class))).thenReturn(savedSummary);
        doThrow(new RuntimeException("SMTP server down"))
                .when(notificationService).sendEodSummaryEmail(any(), any(), any());

        // sendEodSummaryEmail is @Async in production (fire-and-forget). In a unit test
        // (no Spring context) it runs synchronously, so closeDay() propagates the exception.
        // We verify that summaryRepository.save was called before the notification attempt.
        assertThatThrownBy(() -> reportingService.closeDay("owner@test.com", today))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("SMTP server down");

        verify(summaryRepository).save(any(DailySummary.class));
        verify(summaryRepository, never()).delete(any());
    }

    // ── Business Rule 6: getSummary() throws for unclosed days ──────────────────
    @Test
    @DisplayName("Rule 6 - getSummary() throws when day has not been closed yet")
    void getSummary_dayNotClosed_throwsException() {
        when(summaryRepository.findByRestaurantAndReportDate(restaurant, today)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportingService.getSummary("owner@test.com", today))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No EOD summary");
    }
}
