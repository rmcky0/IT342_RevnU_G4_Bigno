package com.revnu.backend.features.reporting.service;

import com.revnu.backend.features.reporting.model.DailySummary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEodReport(String ownerEmail, String restaurantName, DailySummary summary) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(ownerEmail);
        message.setSubject("RevnU: End of Day Report for " + summary.getReportDate());

        String body = String.format("""
                                    Hello,
                                    
                                    Here is your daily summary for %s:
                                    
                                    Total Sales: \u20b1%s
                                    Total Expenses: \u20b1%s
                                    Total Salaries Paid: \u20b1%s
                                    -----------------------------
                                    Net Profit: \u20b1%s
                                    
                                    These records are now locked in the system.
                                    
                                    Best,
                                    The RevnU Team""",
                restaurantName,
                summary.getTotalSales(),
                summary.getTotalExpenses(),
                summary.getTotalSalaries(),
                summary.getNetProfit()
        );

        message.setText(body);
        mailSender.send(message);
    }
}
