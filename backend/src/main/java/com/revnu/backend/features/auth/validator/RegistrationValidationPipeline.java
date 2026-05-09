package com.revnu.backend.features.auth.validator;

import org.springframework.stereotype.Service;

import com.revnu.backend.features.auth.dto.RegisterRequest;


@Service
public class RegistrationValidationPipeline {
    private final EmailExistsValidator emailExistsValidator;

    public RegistrationValidationPipeline(EmailExistsValidator emailExistsValidator) {
        this.emailExistsValidator = emailExistsValidator;
    }

    public void validateRegistration(RegisterRequest request) {
        EmailFormatValidator emailFormatValidator = new EmailFormatValidator();
        emailFormatValidator.setNext(emailExistsValidator);
        emailFormatValidator.validate(request);
    }
}



