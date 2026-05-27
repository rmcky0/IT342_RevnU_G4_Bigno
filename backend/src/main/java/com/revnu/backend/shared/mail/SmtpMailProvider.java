package com.revnu.backend.shared.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "smtp")
public class SmtpMailProvider implements MailProvider {

    private static final Logger logger = LoggerFactory.getLogger(SmtpMailProvider.class);

    private final JavaMailSender mailSender;

    public SmtpMailProvider(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(String to, String from, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            logger.info("Email sent via SMTP to {}", to);
        } catch (MessagingException e) {
            logger.warn("Failed to send email via SMTP to {}: {}", to, e.getMessage());
        }
    }
}
