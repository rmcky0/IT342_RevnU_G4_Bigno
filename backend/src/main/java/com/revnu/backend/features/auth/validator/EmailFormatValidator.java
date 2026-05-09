package com.revnu.backend.features.auth.validator;

import com.revnu.backend.features.auth.dto.RegisterRequest;

public class EmailFormatValidator extends RegistrationValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    protected boolean isValid(RegisterRequest request) {
        return request.email() != null && request.email().matches(EMAIL_REGEX);
    }

    @Override
    protected String getErrorMessage() {
        return "Invalid email format";
    }
}



