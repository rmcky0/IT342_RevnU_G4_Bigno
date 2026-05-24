package com.revnu.backend.features.staff;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.staff.dto.SalaryRequest;
import com.revnu.backend.features.staff.dto.StaffRequest;
import com.revnu.backend.features.staff.model.Salary;
import com.revnu.backend.features.staff.model.SalaryStatus;
import com.revnu.backend.features.staff.model.Staff;
import com.revnu.backend.features.staff.repository.SalaryRepository;
import com.revnu.backend.features.staff.repository.StaffRepository;
import com.revnu.backend.features.staff.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StaffService Unit Tests")
class StaffServiceTest {

    @Mock private StaffRepository staffRepository;
    @Mock private SalaryRepository salaryRepository;
    @Mock private UserRepository userRepository;
    @Mock private RestaurantRepository restaurantRepository;

    @InjectMocks
    private StaffService staffService;

    private User owner;
    private Restaurant restaurant;
    private Restaurant otherRestaurant;
    private Staff activeStaff;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(UUID.randomUUID()).email("owner@test.com").build();
        restaurant = Restaurant.builder().id(UUID.randomUUID()).name("My Restaurant").owner(owner).build();
        otherRestaurant = Restaurant.builder().id(UUID.randomUUID()).name("Other Restaurant").build();

        activeStaff = buildStaff(restaurant, true);

        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(restaurantRepository.findByOwner(owner)).thenReturn(Optional.of(restaurant));
    }

    private Staff buildStaff(Restaurant r, boolean active) {
        Staff staff = new Staff();
        staff.setId(UUID.randomUUID());
        staff.setFullname("John Doe");
        staff.setPosition("Waiter");
        staff.setSalaryRate(new BigDecimal("500.00"));
        staff.setRestaurant(r);
        staff.setActive(active);
        return staff;
    }

    // ── Business Rule 5 & 26: Staff are non-user entities ───────────────────────
    @Test
    @DisplayName("Rule 5/26 - addStaff() creates a staff member without login credentials")
    void addStaff_createsStaffWithoutLoginCredentials() {
        StaffRequest req = new StaffRequest("Jane Smith", "Cashier", new BigDecimal("450.00"));
        Staff saved = buildStaff(restaurant, true);
        when(staffRepository.save(any(Staff.class))).thenReturn(saved);

        staffService.addStaff("owner@test.com", req);

        // Staff is saved but never touches UserRepository — no login account created
        verify(userRepository, never()).save(any(User.class));
        verify(staffRepository).save(any(Staff.class));
    }

    // ── Business Rule 11: Soft delete when staff has salary history ──────────────
    @Test
    @DisplayName("Rule 11 - deleteStaff() soft-deletes (active=false) when salary history exists")
    void deleteStaff_withSalaryHistory_softDeletes() {
        UUID staffId = activeStaff.getId();
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(activeStaff));
        when(salaryRepository.findByStaff(activeStaff)).thenReturn(
                List.of(Salary.builder().id(UUID.randomUUID()).amount(new BigDecimal("500.00")).build()));

        staffService.deleteStaff("owner@test.com", staffId);

        assertThat(activeStaff.isActive()).isFalse();
        verify(staffRepository).save(activeStaff);
        verify(staffRepository, never()).delete(activeStaff);
    }

    // ── Business Rule 11: Hard delete when no salary history exists ──────────────
    @Test
    @DisplayName("Rule 11 - deleteStaff() hard-deletes when no salary history")
    void deleteStaff_noSalaryHistory_hardDeletes() {
        UUID staffId = activeStaff.getId();
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(activeStaff));
        when(salaryRepository.findByStaff(activeStaff)).thenReturn(Collections.emptyList());

        staffService.deleteStaff("owner@test.com", staffId);

        verify(staffRepository).delete(activeStaff);
        verify(staffRepository, never()).save(any(Staff.class));
    }

    // ── Business Rule 12/18: Payout date cannot be in the future ────────────────
    @Test
    @DisplayName("Rule 18 - recordSalaryPayout() rejects future payment dates")
    void recordSalaryPayout_futureDate_throwsIllegalArgument() {
        UUID staffId = activeStaff.getId();
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(activeStaff));

        LocalDate futureDate = LocalDate.now().plusDays(10);
        SalaryRequest req = new SalaryRequest(staffId, new BigDecimal("500.00"), futureDate);

        assertThatThrownBy(() -> staffService.recordSalaryPayout("owner@test.com", req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("future");
    }

    // ── Business Rule 4: EOD-locked salary cannot be modified ───────────────────
    @Test
    @DisplayName("Rule 4 - updateSalaryPayout() throws for EOD-locked (CLOSED) salary")
    void updateSalaryPayout_closedSalary_throwsIllegalState() {
        UUID salaryId = UUID.randomUUID();
        Salary closedSalary = Salary.builder()
                .id(salaryId).amount(new BigDecimal("500.00"))
                .paymentDate(LocalDate.now()).staff(activeStaff)
                .restaurant(restaurant).status(SalaryStatus.CLOSED).build();

        when(salaryRepository.findById(salaryId)).thenReturn(Optional.of(closedSalary));

        SalaryRequest req = new SalaryRequest(activeStaff.getId(), new BigDecimal("600.00"), LocalDate.now());

        assertThatThrownBy(() -> staffService.updateSalaryPayout("owner@test.com", salaryId, req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EOD-locked");
    }

    // ── Business Rule 4: EOD-locked salary cannot be deleted ────────────────────
    @Test
    @DisplayName("Rule 4 - deleteSalaryPayout() throws for EOD-locked (CLOSED) salary")
    void deleteSalaryPayout_closedSalary_throwsIllegalState() {
        UUID salaryId = UUID.randomUUID();
        Salary closedSalary = Salary.builder()
                .id(salaryId).amount(new BigDecimal("500.00"))
                .paymentDate(LocalDate.now()).staff(activeStaff)
                .restaurant(restaurant).status(SalaryStatus.CLOSED).build();

        when(salaryRepository.findById(salaryId)).thenReturn(Optional.of(closedSalary));

        assertThatThrownBy(() -> staffService.deleteSalaryPayout("owner@test.com", salaryId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EOD-locked");
    }

    // ── Business Rule 14: Payout must link to an existing staff member ───────────
    @Test
    @DisplayName("Rule 14 - recordSalaryPayout() throws when staff does not exist")
    void recordSalaryPayout_nonExistentStaff_throwsException() {
        UUID badStaffId = UUID.randomUUID();
        when(staffRepository.findById(badStaffId)).thenReturn(Optional.empty());

        SalaryRequest req = new SalaryRequest(badStaffId, new BigDecimal("500.00"), LocalDate.now());

        assertThatThrownBy(() -> staffService.recordSalaryPayout("owner@test.com", req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Staff member not found");
    }

    // ── Business Rule 2: Tenant isolation for salary records ────────────────────
    @Test
    @DisplayName("Rule 2 - updateSalaryPayout() throws SecurityException for another restaurant's salary")
    void updateSalaryPayout_foreignRestaurant_throwsSecurityException() {
        UUID salaryId = UUID.randomUUID();
        Salary foreignSalary = Salary.builder()
                .id(salaryId).amount(new BigDecimal("500.00"))
                .paymentDate(LocalDate.now()).staff(activeStaff)
                .restaurant(otherRestaurant).status(SalaryStatus.OPEN).build();

        when(salaryRepository.findById(salaryId)).thenReturn(Optional.of(foreignSalary));

        SalaryRequest req = new SalaryRequest(activeStaff.getId(), new BigDecimal("600.00"), LocalDate.now());

        assertThatThrownBy(() -> staffService.updateSalaryPayout("owner@test.com", salaryId, req))
                .isInstanceOf(SecurityException.class);
    }

    // ── Business Rule 11: Salary rate update only affects future payouts ─────────
    @Test
    @DisplayName("Rule 11 - updateStaff() updates salary rate without touching existing salary records")
    void updateStaff_salaryRateChange_doesNotAffectHistoricalSalaries() {
        UUID staffId = activeStaff.getId();
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(activeStaff));
        when(staffRepository.save(any(Staff.class))).thenReturn(activeStaff);

        StaffRequest req = new StaffRequest("John Doe", "Waiter", new BigDecimal("750.00"));

        staffService.updateStaff("owner@test.com", staffId, req);

        // Salary records are untouched — only staffRepository.save is called
        verify(salaryRepository, never()).save(any(Salary.class));
        verify(salaryRepository, never()).saveAll(any());
    }
}
