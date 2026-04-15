package com.revnu.backend.validator;

import com.revnu.backend.dto.RegisterRequest;

public class EmailFormatValidator extends RegistrationValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    protected boolean isValid(RegisterRequest request) {
        return request.getEmail() != null && request.getEmail().matches(EMAIL_REGEX);
    }

    @Override
    protected String getErrorMessage() {
        return "Invalid email format";
    }
}
