package com.revnu.backend.validator;

import com.revnu.backend.dto.RegisterRequest;

public abstract class RegistrationValidator {
    protected RegistrationValidator nextValidator;

    public void setNext(RegistrationValidator nextValidator) {
        this.nextValidator = nextValidator;
    }

    public void validate(RegisterRequest request) {
        if (!isValid(request)) {
            throw new IllegalArgumentException(getErrorMessage());
        }

        if (nextValidator != null) {
            nextValidator.validate(request);
        }
    }

    protected abstract boolean isValid(RegisterRequest request);
    protected abstract String getErrorMessage();
}
