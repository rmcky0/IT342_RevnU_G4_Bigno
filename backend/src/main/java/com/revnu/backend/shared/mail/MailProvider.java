package com.revnu.backend.shared.mail;

public interface MailProvider {
    void send(String to, String from, String subject, String html);
}
