package com.revnu.backend.strategy;

import org.springframework.stereotype.Service;

import com.revnu.backend.model.RoleType;

@Service
public class RoleAssignmentService {
    private final FirstUserOwnerStrategy firstUserOwnerStrategy;
    private final DefaultStaffStrategy defaultStaffStrategy;

    public RoleAssignmentService(FirstUserOwnerStrategy firstUserOwnerStrategy,
                                 DefaultStaffStrategy defaultStaffStrategy) {
        this.firstUserOwnerStrategy = firstUserOwnerStrategy;
        this.defaultStaffStrategy = defaultStaffStrategy;
    }

    public RoleType assignRole() {
        RoleType role = firstUserOwnerStrategy.assignRole();
        if (role != null) {
            return role;
        }
        return defaultStaffStrategy.assignRole();
    }
}
