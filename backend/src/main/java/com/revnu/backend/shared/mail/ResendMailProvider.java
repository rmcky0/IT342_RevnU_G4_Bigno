package com.revnu.backend.shared.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;

@Component
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "resend")
public class ResendMailProvider implements MailProvider {

    private static final Logger logger = LoggerFactory.getLogger(ResendMailProvider.class);

    private final Resend resend;

    public ResendMailProvider(@Value("${resend.api-key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    @Override
    public void send(String to, String from, String subject, String html) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(from)
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();
            resend.emails().send(params);
            logger.info("Email sent via Resend to {}", to);
        } catch (Exception e) {
            logger.warn("Failed to send email via Resend to {}: {}", to, e.getMessage());
        }
    }
}
