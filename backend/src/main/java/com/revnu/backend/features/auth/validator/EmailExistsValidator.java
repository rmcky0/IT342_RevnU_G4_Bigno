package com.revnu.backend.features.auth.validator;

import org.springframework.stereotype.Component;

import com.revnu.backend.features.auth.dto.RegisterRequest;
import com.revnu.backend.features.auth.repository.UserRepository;


@Component
public class EmailExistsValidator extends RegistrationValidator {
    private final UserRepository userRepository;

    public EmailExistsValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected boolean isValid(RegisterRequest request) {
        return !userRepository.existsByEmail(request.email());
    }

    @Override
    protected String getErrorMessage() {
        return "Email already taken";
    }
}



