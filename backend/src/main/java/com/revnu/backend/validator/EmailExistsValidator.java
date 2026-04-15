package com.revnu.backend.validator;

import org.springframework.stereotype.Component;

import com.revnu.backend.dto.RegisterRequest;
import com.revnu.backend.repository.UserRepository;


@Component
public class EmailExistsValidator extends RegistrationValidator {
    private final UserRepository userRepository;

    public EmailExistsValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected boolean isValid(RegisterRequest request) {
        return !userRepository.existsByEmail(request.getEmail());
    }

    @Override
    protected String getErrorMessage() {
        return "Email already taken";
    }
}
