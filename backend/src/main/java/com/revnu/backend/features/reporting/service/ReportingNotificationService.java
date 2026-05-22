package com.revnu.backend.features.reporting.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.notifications.service.NotificationService;
import com.revnu.backend.features.reporting.model.DailySummary;
import com.revnu.backend.features.restaurants.model.Restaurant;

@Service
public class ReportingNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(ReportingNotificationService.class);

    private final EmailService emailService;
    private final NotificationService notificationService;

    public ReportingNotificationService(EmailService emailService, NotificationService notificationService) {
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @Async
    public void sendEodSummaryEmail(User owner, Restaurant restaurant, DailySummary summary) {
        try {
            logger.info("Sending EOD summary email to {} for {}", owner.getEmail(), restaurant.getName());
            emailService.sendEodReport(owner.getEmail(), restaurant.getName(), summary);
            notificationService.createEodEmailSentNotification(owner, restaurant, summary);
            logger.info("EOD email sent successfully to {}", owner.getEmail());
        } catch (Exception e) {
            logger.warn(
                    "Failed to send EOD email to {} for restaurant {}: {}",
                    owner.getEmail(), restaurant.getName(), e.getMessage()
            );

        }
    }

}
