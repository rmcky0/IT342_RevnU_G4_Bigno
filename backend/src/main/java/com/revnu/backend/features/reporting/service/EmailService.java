package com.revnu.backend.features.reporting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.revnu.backend.features.reporting.email.EodReportEmailTemplate;
import com.revnu.backend.features.reporting.email.OtpEmailTemplate;
import com.revnu.backend.features.reporting.email.ReactivationEmailTemplate;
import com.revnu.backend.features.reporting.email.SuspensionEmailTemplate;
import com.revnu.backend.features.reporting.email.WelcomeEmailTemplate;
import com.revnu.backend.features.reporting.model.DailySummary;
import com.revnu.backend.shared.mail.MailProvider;

@Service
public class EmailService {

    private final MailProvider mailProvider;
    private final String fromAddress;

    public EmailService(MailProvider mailProvider,
            @Value("${app.mail.from}") String fromAddress) {
        this.mailProvider = mailProvider;
        this.fromAddress = fromAddress;
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String fullname) {
        new WelcomeEmailTemplate(fullname).send(toEmail, mailProvider, fromAddress);
    }

    @Async
    public void sendSuspensionEmail(String toEmail, String fullname) {
        new SuspensionEmailTemplate(fullname).send(toEmail, mailProvider, fromAddress);
    }

    @Async
    public void sendReactivationEmail(String toEmail, String fullname) {
        new ReactivationEmailTemplate(fullname).send(toEmail, mailProvider, fromAddress);
    }

    @Async
    public void sendOtpEmail(String toEmail, String fullname, String otp) {
        new OtpEmailTemplate(fullname, otp).send(toEmail, mailProvider, fromAddress);
    }

    public void sendEodReport(String ownerEmail, String restaurantName, DailySummary summary) {
        new EodReportEmailTemplate(ownerEmail, restaurantName, summary).send(ownerEmail, mailProvider, fromAddress);
    }
}
