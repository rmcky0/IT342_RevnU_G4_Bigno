package com.revnu.backend.features.auth.validator;

import com.revnu.backend.features.auth.dto.RegisterRequest;

public class PasswordStrengthValidator extends RegistrationValidator {

    private static final java.util.regex.Pattern STRONG_PASSWORD =
        java.util.regex.Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$"
        );

    @Override
    protected boolean isValid(RegisterRequest request) {
        return request.password() != null && STRONG_PASSWORD.matcher(request.password()).matches();
    }

    @Override
    protected String getErrorMessage() {
        return "Password must be at least 8 characters and include uppercase, lowercase, a number, and a special character.";
    }
}
