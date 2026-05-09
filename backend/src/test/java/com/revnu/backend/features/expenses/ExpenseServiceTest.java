package com.revnu.backend.features.expenses;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.expenses.dto.ExpenseRequest;
import com.revnu.backend.features.expenses.dto.ExpenseResponse;
import com.revnu.backend.features.expenses.model.Expense;
import com.revnu.backend.features.expenses.model.ExpenseStatus;
import com.revnu.backend.features.expenses.repository.ExpenseRepository;
import com.revnu.backend.features.expenses.service.ExpenseService;
import com.revnu.backend.features.files.repository.FileRecordRepository;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.tags.repository.TagRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseService Tests")
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private FileRecordRepository fileRecordRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private User testUser;
    private Restaurant testRestaurant;
    private UUID testExpenseId;

    @BeforeEach
    void setUp() {
        testExpenseId = UUID.randomUUID();

        testUser = new User();
        testUser.setEmail("tenant@revnu.com");
        testUser.setFullname("Test Tenant");
        testUser.setRole(RoleType.TENANT);

        testRestaurant = new Restaurant();
        testRestaurant.setId(UUID.randomUUID());
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setOwner(testUser);
    }

    private Expense buildExpense(BigDecimal amount, ExpenseStatus status) {
        Expense e = new Expense();
        e.setId(testExpenseId);
        e.setAmount(amount);
        e.setDescription("Test expense");
        e.setTags(Set.of());
        e.setRestaurant(testRestaurant);
        e.setStatus(status);
        return e;
    }

    private void stubUserAndRestaurant() {
        when(userRepository.findByEmail("tenant@revnu.com"))
                .thenReturn(Optional.of(testUser));
        when(restaurantRepository.findByOwner(testUser))
                .thenReturn(Optional.of(testRestaurant));
    }

    @Test
    @DisplayName("Record expense — saves and returns ExpenseResponse")
    void recordExpense_validRequest_savesAndReturnsResponse() {
        stubUserAndRestaurant();
        ExpenseRequest request = new ExpenseRequest(
                new BigDecimal("200.00"), List.of(), "Supplies");

        Expense saved = buildExpense(new BigDecimal("200.00"), ExpenseStatus.OPEN);
        when(expenseRepository.save(any(Expense.class))).thenReturn(saved);

        ExpenseResponse result = expenseService.recordExpense("tenant@revnu.com", request);

        assertNotNull(result, "Result should not be null");
        assertEquals(0, new BigDecimal("200.00").compareTo(result.amount()),
                "Amount should be 200.00");
        assertEquals(ExpenseStatus.OPEN, result.status(), "New expense should be OPEN");

        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    @DisplayName("Record expense — user not found throws exception")
    void recordExpense_unknownUser_throwsRuntimeException() {

        when(userRepository.findByEmail("ghost@revnu.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, ()
                -> expenseService.recordExpense("ghost@revnu.com",
                        new ExpenseRequest(new BigDecimal("50.00"), List.of(), "Test"))
        );

        verify(expenseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get all expenses — returns paginated response")
    void getAllExpenses_validOwner_returnsPage() {
        stubUserAndRestaurant();

        List<Expense> expenseList = List.of(
                buildExpense(new BigDecimal("100.00"), ExpenseStatus.OPEN),
                buildExpense(new BigDecimal("50.00"), ExpenseStatus.OPEN)
        );
        when(expenseRepository.findByRestaurant(eq(testRestaurant), any(Pageable.class)))
                .thenReturn(new PageImpl<>(expenseList));

        Page<ExpenseResponse> result = expenseService.getAllExpenses("tenant@revnu.com", 0, 9);

        assertNotNull(result);
        assertEquals(2, result.getContent().size(), "Should return 2 expenses");
    }

    @Test
    @DisplayName("Update open expense — changes amount successfully")
    void updateExpense_openExpense_updatesSuccessfully() {
        stubUserAndRestaurant();

        Expense existing = buildExpense(new BigDecimal("100.00"), ExpenseStatus.OPEN);
        when(expenseRepository.findById(testExpenseId)).thenReturn(Optional.of(existing));

        Expense updated = buildExpense(new BigDecimal("300.00"), ExpenseStatus.OPEN);
        when(expenseRepository.save(any(Expense.class))).thenReturn(updated);

        ExpenseResponse result = expenseService.updateExpense("tenant@revnu.com", testExpenseId,
                new ExpenseRequest(new BigDecimal("300.00"), List.of(), "Updated cost"));

        assertNotNull(result);
        assertEquals(0, new BigDecimal("300.00").compareTo(result.amount()));
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    @DisplayName("Update CLOSED expense — throws IllegalStateException")
    void updateExpense_closedExpense_throwsIllegalStateException() {
        stubUserAndRestaurant();

        Expense closed = buildExpense(new BigDecimal("100.00"), ExpenseStatus.CLOSED);
        when(expenseRepository.findById(testExpenseId)).thenReturn(Optional.of(closed));

        assertThrows(IllegalStateException.class, ()
                -> expenseService.updateExpense("tenant@revnu.com", testExpenseId,
                        new ExpenseRequest(new BigDecimal("300.00"), List.of(), "Try edit")),
                "Should not allow editing finalized expense"
        );

        verify(expenseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete open expense — removes record")
    void deleteExpense_openExpense_deletesSuccessfully() {
        stubUserAndRestaurant();

        Expense open = buildExpense(new BigDecimal("100.00"), ExpenseStatus.OPEN);
        when(expenseRepository.findById(testExpenseId)).thenReturn(Optional.of(open));

        expenseService.deleteExpense("tenant@revnu.com", testExpenseId);

        verify(expenseRepository, times(1)).delete(open);
    }

    @Test
    @DisplayName("Delete CLOSED expense — throws IllegalStateException")
    void deleteExpense_closedExpense_throwsIllegalStateException() {
        stubUserAndRestaurant();

        Expense closed = buildExpense(new BigDecimal("100.00"), ExpenseStatus.CLOSED);
        when(expenseRepository.findById(testExpenseId)).thenReturn(Optional.of(closed));

        assertThrows(IllegalStateException.class, ()
                -> expenseService.deleteExpense("tenant@revnu.com", testExpenseId),
                "Should not delete a finalized expense"
        );

        verify(expenseRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Delete expense — wrong restaurant throws SecurityException")
    void deleteExpense_wrongRestaurant_throwsSecurityException() {
        stubUserAndRestaurant();

        Restaurant otherRestaurant = new Restaurant();
        otherRestaurant.setId(UUID.randomUUID());

        Expense foreignExpense = new Expense();
        foreignExpense.setId(testExpenseId);
        foreignExpense.setAmount(new BigDecimal("100.00"));
        foreignExpense.setRestaurant(otherRestaurant);
        foreignExpense.setStatus(ExpenseStatus.OPEN);

        when(expenseRepository.findById(testExpenseId))
                .thenReturn(Optional.of(foreignExpense));

        assertThrows(SecurityException.class, ()
                -> expenseService.deleteExpense("tenant@revnu.com", testExpenseId),
                "Should throw SecurityException for unauthorized delete"
        );

        verify(expenseRepository, never()).delete(any());
    }
}
