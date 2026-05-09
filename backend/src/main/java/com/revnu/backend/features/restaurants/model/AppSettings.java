package com.revnu.backend.features.restaurants.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class AppSettings {

    private String language = "en";
    private String theme = "light";
    private boolean emailNotifications = true;
    private boolean pushNotifications = false;
    private boolean requireReceiptPhoto = false;
    private boolean softLockRecords = true;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public boolean isPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public boolean isRequireReceiptPhoto() {
        return requireReceiptPhoto;
    }

    public void setRequireReceiptPhoto(boolean requireReceiptPhoto) {
        this.requireReceiptPhoto = requireReceiptPhoto;
    }

    public boolean isSoftLockRecords() {
        return softLockRecords;
    }

    public void setSoftLockRecords(boolean softLockRecords) {
        this.softLockRecords = softLockRecords;
    }
}
