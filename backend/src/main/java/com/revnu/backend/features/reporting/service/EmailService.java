package com.revnu.backend.features.reporting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.revnu.backend.features.reporting.email.EodReportEmailTemplate;
import com.revnu.backend.features.reporting.email.OtpEmailTemplate;
import com.revnu.backend.features.reporting.email.ReactivationEmailTemplate;
import com.revnu.backend.features.reporting.email.SuspensionEmailTemplate;
import com.revnu.backend.features.reporting.email.WelcomeEmailTemplate;
import com.revnu.backend.features.reporting.model.DailySummary;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String fullname) {
        new WelcomeEmailTemplate(fullname).send(toEmail, mailSender, fromAddress);
    }

    @Async
    public void sendSuspensionEmail(String toEmail, String fullname) {
        new SuspensionEmailTemplate(fullname).send(toEmail, mailSender, fromAddress);
    }

    @Async
    public void sendReactivationEmail(String toEmail, String fullname) {
        new ReactivationEmailTemplate(fullname).send(toEmail, mailSender, fromAddress);
    }

    @Async
    public void sendOtpEmail(String toEmail, String fullname, String otp) {
        new OtpEmailTemplate(fullname, otp).send(toEmail, mailSender, fromAddress);
    }

    public void sendEodReport(String ownerEmail, String restaurantName, DailySummary summary) {
        new EodReportEmailTemplate(ownerEmail, restaurantName, summary).send(ownerEmail, mailSender, fromAddress);
    }
}
