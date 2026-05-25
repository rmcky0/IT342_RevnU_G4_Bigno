package com.revnu.backend.features.reporting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.revnu.backend.features.reporting.email.EodReportEmailTemplate;
import com.revnu.backend.features.reporting.email.OtpEmailTemplate;
import com.revnu.backend.features.reporting.email.ReactivationEmailTemplate;
import com.revnu.backend.features.reporting.email.SuspensionEmailTemplate;
import com.revnu.backend.features.reporting.email.WelcomeEmailTemplate;
import com.revnu.backend.features.reporting.model.DailySummary;

@Service
public class EmailService {

    private final Resend resend;
    private final String fromAddress;

    public EmailService(@Value("${resend.api-key}") String apiKey,
            @Value("${resend.from-address}") String fromAddress) {
        this.resend = new Resend(apiKey);
        this.fromAddress = fromAddress;
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String fullname) {
        new WelcomeEmailTemplate(fullname).send(toEmail, resend, fromAddress);
    }

    @Async
    public void sendSuspensionEmail(String toEmail, String fullname) {
        new SuspensionEmailTemplate(fullname).send(toEmail, resend, fromAddress);
    }

    @Async
    public void sendReactivationEmail(String toEmail, String fullname) {
        new ReactivationEmailTemplate(fullname).send(toEmail, resend, fromAddress);
    }

    @Async
    public void sendOtpEmail(String toEmail, String fullname, String otp) {
        new OtpEmailTemplate(fullname, otp).send(toEmail, resend, fromAddress);
    }

    public void sendEodReport(String ownerEmail, String restaurantName, DailySummary summary) {
        new EodReportEmailTemplate(ownerEmail, restaurantName, summary).send(ownerEmail, resend, fromAddress);
    }
}
