package com.revnu.backend.features.staff.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.staff.dto.SalaryRequest;
import com.revnu.backend.features.staff.dto.SalaryResponse;
import com.revnu.backend.features.staff.dto.StaffProfileResponse;
import com.revnu.backend.features.staff.dto.StaffRequest;
import com.revnu.backend.features.staff.model.Salary;
import com.revnu.backend.features.staff.model.SalaryStatus;
import com.revnu.backend.features.staff.model.Staff;
import com.revnu.backend.features.staff.repository.SalaryRepository;
import com.revnu.backend.features.staff.repository.StaffRepository;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final SalaryRepository salaryRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public StaffService(StaffRepository staffRepository, SalaryRepository salaryRepository,
            UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.staffRepository = staffRepository;
        this.salaryRepository = salaryRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // ── Staff CRUD ────────────────────────────────────────────────────────────
    @Transactional
    public StaffProfileResponse addStaff(String ownerEmail, StaffRequest request) {
        Restaurant restaurant = getRestaurant(ownerEmail);

        Staff staff = Staff.builder()
                .fullname(request.fullname())
                .position(request.position())
                .salaryRate(request.salaryRate())
                .restaurant(restaurant)
                .build();

        return mapToStaffResponse(staffRepository.save(staff));
    }

    @Transactional(readOnly = true)
    public List<StaffProfileResponse> getStaff(String ownerEmail) {
        return staffRepository.findByRestaurantAndActiveTrue(getRestaurant(ownerEmail)).stream()
                .map(this::mapToStaffResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StaffProfileResponse getStaffById(String ownerEmail, UUID staffId) {
        Restaurant restaurant = getRestaurant(ownerEmail);
        Staff staff = getValidatedStaff(staffId, restaurant);

        List<SalaryResponse> salaryHistory = salaryRepository.findByStaff(staff).stream()
                .map(this::mapToSalaryResponse).collect(Collectors.toList());

        return new StaffProfileResponse(staff.getId(), staff.getFullname(),
                staff.getPosition(), staff.getSalaryRate(), salaryHistory);
    }

    @Transactional
    public StaffProfileResponse updateStaff(String ownerEmail, UUID staffId, StaffRequest request) {
        Restaurant restaurant = getRestaurant(ownerEmail);
        Staff staff = getValidatedStaff(staffId, restaurant);

        staff.setFullname(request.fullname());
        staff.setPosition(request.position());
        staff.setSalaryRate(request.salaryRate());

        return mapToStaffResponse(staffRepository.save(staff));
    }

    @Transactional
    public void deleteStaff(String ownerEmail, UUID staffId) {
        Restaurant restaurant = getRestaurant(ownerEmail);
        Staff staff = getValidatedStaff(staffId, restaurant);

        if (!salaryRepository.findByStaff(staff).isEmpty()) {
            staff.setActive(false);
            staffRepository.save(staff);
            return;
        }

        staffRepository.delete(staff);
    }

    // ── Salary Payouts ────────────────────────────────────────────────────────
    @Transactional
    public SalaryResponse recordSalaryPayout(String ownerEmail, SalaryRequest request) {
        Restaurant restaurant = getRestaurant(ownerEmail);
        Staff staff = getValidatedStaff(request.staffId(), restaurant);

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Manila"));
        if (request.paymentDate().isAfter(today)) {
            throw new IllegalArgumentException("Payment date cannot be in the future.");
        }

        Salary salary = Salary.builder()
                .amount(request.amount())
                .paymentDate(request.paymentDate())
                .staff(staff)
                .restaurant(restaurant)
                .status(SalaryStatus.OPEN)
                .build();

        return mapToSalaryResponse(salaryRepository.save(salary));
    }

    @Transactional(readOnly = true)
    public Page<SalaryResponse> getSalaryHistory(String ownerEmail, int page, int size) {
        Restaurant restaurant = getRestaurant(ownerEmail);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return salaryRepository.findByRestaurant(restaurant, pageable)
                .map(this::mapToSalaryResponse);
    }

    @Transactional
    public SalaryResponse updateSalaryPayout(String ownerEmail, UUID salaryId, SalaryRequest request) {
        Restaurant restaurant = getRestaurant(ownerEmail);
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("Salary record not found."));
        if (!salary.getRestaurant().getId().equals(restaurant.getId())) {
            throw new SecurityException("Unauthorized access to this salary record.");
        }
        if (salary.getStatus() != SalaryStatus.OPEN) {
            throw new IllegalStateException("Cannot modify a salary that has been EOD-locked.");
        }

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Manila"));
        if (request.paymentDate().isAfter(today)) {
            throw new IllegalArgumentException("Payment date cannot be in the future.");
        }

        salary.setAmount(request.amount());
        salary.setPaymentDate(request.paymentDate());
        return mapToSalaryResponse(salaryRepository.save(salary));
    }

    @Transactional
    public void deleteSalaryPayout(String ownerEmail, UUID salaryId) {
        Restaurant restaurant = getRestaurant(ownerEmail);
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("Salary record not found."));
        if (!salary.getRestaurant().getId().equals(restaurant.getId())) {
            throw new SecurityException("Unauthorized access to this salary record.");
        }
        if (salary.getStatus() != SalaryStatus.OPEN) {
            throw new IllegalStateException("Cannot delete a salary that has been EOD-locked.");
        }
        salaryRepository.delete(salary);
    }

    @Transactional(readOnly = true)
    public List<SalaryResponse> getStaffSalaryHistory(String ownerEmail, UUID staffId) {
        Restaurant restaurant = getRestaurant(ownerEmail);
        Staff staff = getValidatedStaff(staffId, restaurant);

        return salaryRepository.findByStaff(staff).stream()
                .map(this::mapToSalaryResponse).collect(Collectors.toList());
    }

    // ── Helper Methods ────────────────────────────────────────────────────────
    private Restaurant getRestaurant(String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return restaurantRepository.findByOwner(owner)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant profile not found."));
    }

    private Staff getValidatedStaff(UUID staffId, Restaurant restaurant) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found."));
        if (!staff.isActive()) {
            throw new IllegalArgumentException("Staff member not found.");
        }
        if (!staff.getRestaurant().getId().equals(restaurant.getId())) {
            throw new SecurityException("Unauthorized access to this staff profile.");
        }
        return staff;
    }

    private StaffProfileResponse mapToStaffResponse(Staff staff) {
        return new StaffProfileResponse(staff.getId(), staff.getFullname(),
                staff.getPosition(), staff.getSalaryRate(), Collections.emptyList());
    }

    private SalaryResponse mapToSalaryResponse(Salary salary) {
        return new SalaryResponse(salary.getId(), salary.getStaff().getId(),
                salary.getStaff().getFullname(), salary.getAmount(),
                salary.getPaymentDate(), salary.getStatus(), salary.getCreatedAt());
    }
}
