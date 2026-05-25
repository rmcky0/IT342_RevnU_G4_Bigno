package com.revnu.backend.features.auth.observer;

import org.springframework.stereotype.Component;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.reporting.service.EmailService;

@Component
public class WelcomeEmailListener implements AuthEventListener {

    private final EmailService emailService;

    public WelcomeEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onUserRegistered(User user) {
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullname());
    }
}
