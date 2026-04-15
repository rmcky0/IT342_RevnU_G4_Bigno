package com.revnu.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revnu.backend.model.EmploymentStatus;
import com.revnu.backend.model.Staff;
import com.revnu.backend.model.User;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {
    List<Staff> findByUser(User user);
    
    List<Staff> findByUserAndStatus(User user, EmploymentStatus status);
    
    List<Staff> findByUserAndPosition(User user, String position);
}
