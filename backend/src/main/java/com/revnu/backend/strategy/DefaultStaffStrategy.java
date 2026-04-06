package com.revnu.backend.strategy;

import org.springframework.stereotype.Component;

import com.revnu.backend.model.RoleType;

@Component
public class DefaultStaffStrategy implements RoleAssignmentStrategy {

    @Override
    public RoleType assignRole() {
        return RoleType.STAFF;
    }
}
