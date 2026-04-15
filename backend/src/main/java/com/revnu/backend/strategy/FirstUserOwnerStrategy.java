package com.revnu.backend.strategy;

import org.springframework.stereotype.Component;

import com.revnu.backend.model.RoleType;
import com.revnu.backend.repository.UserRepository;

@Component
public class FirstUserOwnerStrategy implements RoleAssignmentStrategy {
    private final UserRepository userRepository;

    public FirstUserOwnerStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public RoleType assignRole() {
        if (userRepository.count() == 0) {
            return RoleType.OWNER;
        }
        return null; 
    }
}
