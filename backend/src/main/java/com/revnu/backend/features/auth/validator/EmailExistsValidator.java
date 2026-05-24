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
    public void validate(RegisterRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(existing -> {
            if ("google".equals(existing.getProvider())) {
                throw new IllegalArgumentException(
                    "This email is registered with Google Sign-In. Please use the 'Continue with Google' button to sign in.");
            }
            throw new IllegalArgumentException(getErrorMessage());
        });
        if (nextValidator != null) {
            nextValidator.validate(request);
        }
    }

    @Override
    protected boolean isValid(RegisterRequest request) {
        return userRepository.findByEmail(request.email()).isEmpty();
    }

    @Override
    protected String getErrorMessage() {
        return "Email already taken";
    }
}
