package com.revnu.backend.features.auth.observer;

import org.springframework.stereotype.Component;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.notifications.service.NotificationService;

@Component
public class AdminNotifyListener implements AuthEventListener {

    private final NotificationService notificationService;

    public AdminNotifyListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onUserRegistered(User user) {
        notificationService.notifyAdminsUserRegistered(user);
    }
}
