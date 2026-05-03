package com.revnu.backend.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.revnu.backend.dto.StaffRequest;
import com.revnu.backend.dto.StaffResponse;
import com.revnu.backend.model.EmploymentStatus;
import com.revnu.backend.model.Staff;
import com.revnu.backend.model.User;
import com.revnu.backend.repository.StaffRepository;
import com.revnu.backend.repository.UserRepository;

@Service
public class StaffService {
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    public StaffService(StaffRepository staffRepository, UserRepository userRepository) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
    }

    public StaffResponse createStaff(String userEmail, StaffRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        EmploymentStatus status = EmploymentStatus.valueOf(request.getStatus().toUpperCase());
        Staff staff = new Staff(request.getStaffName(), request.getPosition(), request.getSalary(), request.getPaymentFrequency(), request.getHireDate(), status, user);
        Staff saved = staffRepository.save(staff);
        return toResponse(saved);
    }

    public List<StaffResponse> getStaffByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return staffRepository.findByUser(user).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<StaffResponse> getActiveStaff(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return staffRepository.findByUserAndStatus(user, EmploymentStatus.ACTIVE).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<StaffResponse> getStaffByPosition(String userEmail, String position) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return staffRepository.findByUserAndPosition(user, position).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public StaffResponse getStaffById(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff record not found"));
        return toResponse(staff);
    }

    public StaffResponse updateStaff(UUID id, StaffRequest request) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff record not found"));

        staff.setStaffName(request.getStaffName());
        staff.setPosition(request.getPosition());
        staff.setSalary(request.getSalary());
        staff.setPaymentFrequency(request.getPaymentFrequency());
        staff.setHireDate(request.getHireDate());
        staff.setStatus(EmploymentStatus.valueOf(request.getStatus().toUpperCase()));

        Staff updated = staffRepository.save(staff);
        return toResponse(updated);
    }

    public void deleteStaff(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff record not found"));
        staffRepository.delete(staff);
    }

    private StaffResponse toResponse(Staff staff) {
        return new StaffResponse(staff.getId(), staff.getStaffName(), staff.getPosition(), staff.getSalary(), staff.getPaymentFrequency(), staff.getHireDate(), staff.getStatus().name(), staff.getCreatedAt(), staff.getUpdatedAt());
    }
}
