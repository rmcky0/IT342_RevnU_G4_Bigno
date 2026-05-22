package com.revnu.backend.features.reporting.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.revnu.backend.features.reporting.model.DailySummary;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    // ── Shared layout ─────────────────────────────────────────────────────────
    private String wrap(String accentColor, String iconSvg, String title, String bodyHtml) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <title>RevnU</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f8fafc;font-family:system-ui,-apple-system,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f8fafc;padding:40px 16px;">
                    <tr><td align="center">
                      <table width="100%%" cellpadding="0" cellspacing="0" style="max-width:600px;background-color:#ffffff;border-radius:12px;border:1px solid #e2e8f0;overflow:hidden;">
                        
                        <tr>
                          <td style="background-color:#1e1b4b;padding:32px 40px;text-align:center;">
                            <span style="font-size:28px;font-weight:800;color:#ffffff;letter-spacing:0.5px;">RevnU</span>
                            <p style="margin:6px 0 0;font-size:13px;color:#a5b4fc;letter-spacing:1.5px;text-transform:uppercase;font-weight:600;">Restaurant Revenue Tracker</p>
                          </td>
                        </tr>

                        <tr>
                          <td style="padding:48px 40px 40px;">
                            
                            <div style="text-align:center;margin-bottom:28px;">
                              <div style="display:inline-block;background-color:%s;border-radius:50%%;padding:18px;line-height:0;">
                                %s
                              </div>
                            </div>

                            <h1 style="margin:0 0 20px;text-align:center;font-size:24px;font-weight:700;color:#0f172a;">%s</h1>

                            %s

                          </td>
                        </tr>

                        <tr>
                          <td style="background-color:#f1f5f9;border-top:1px solid #e2e8f0;padding:24px 40px;text-align:center;">
                            <p style="margin:0;font-size:13px;color:#64748b;line-height:1.5;">
                              You received this email because you have a RevnU account.<br/>
                              &copy; 2026 RevnU &nbsp;&bull;&nbsp; All rights reserved.
                            </p>
                          </td>
                        </tr>

                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(accentColor, iconSvg, title, bodyHtml);
    }

    private void sendHtml(String to, String subject, String html) throws Exception {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
        helper.setFrom(fromAddress, "RevnU Team");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(mime);
    }

    // ── Emails ────────────────────────────────────────────────────────────────
    @Async
    public void sendWelcomeEmail(String toEmail, String fullname) {
        try {
            String icon = "<svg width='32' height='32' viewBox='0 0 24 24' fill='none' stroke='#6366f1' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'/><circle cx='12' cy='7' r='4'/></svg>";
            String body = """
                    <p style="margin:0 0 16px;font-size:16px;color:#334155;line-height:1.6;">
                      Hi <strong>%s</strong>,
                    </p>
                    <p style="margin:0 0 24px;font-size:16px;color:#334155;line-height:1.6;">
                      Welcome to <strong style="color:#0f172a;">RevnU</strong>! Your account has been created successfully.
                      You can now log in and set up your restaurant profile to start tracking your sales and expenses.
                    </p>
                    <div style="background-color:#eef2ff;border-radius:8px;padding:20px;margin-bottom:28px;">
                      <p style="margin:0;font-size:15px;color:#4338ca;line-height:1.7;">
                        <strong>Getting started:</strong><br/>
                        &#10003;&nbsp; Log in to your account<br/>
                        &#10003;&nbsp; Set up your restaurant profile<br/>
                        &#10003;&nbsp; Start recording sales and expenses
                      </p>
                    </div>
                    <p style="margin:0;font-size:14px;color:#64748b;">
                      If you did not create this account, you can safely ignore this email.
                    </p>
                    """.formatted(fullname);

            String html = wrap("#e0e7ff", icon, "Welcome to RevnU!", body);
            sendHtml(toEmail, "Welcome to RevnU!", html);
            logger.info("Welcome email sent to {}", toEmail);
        } catch (Exception e) {
            logger.warn("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendSuspensionEmail(String toEmail, String fullname) {
        try {
            String icon = "<svg width='32' height='32' viewBox='0 0 24 24' fill='none' stroke='#ea580c' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><rect x='3' y='11' width='18' height='11' rx='2' ry='2'/><path d='M7 11V7a5 5 0 0 1 10 0v4'/></svg>";
            String body = """
                    <p style="margin:0 0 16px;font-size:16px;color:#334155;line-height:1.6;">
                      Hi <strong>%s</strong>,
                    </p>
                    <p style="margin:0 0 24px;font-size:16px;color:#334155;line-height:1.6;">
                      Your <strong style="color:#0f172a;">RevnU</strong> account has been <strong>suspended</strong> by an administrator.
                      You will not be able to log in until your account is reactivated.
                    </p>
                    <div style="background-color:#fff7ed;border-left:4px solid #f97316;border-radius:0 8px 8px 0;padding:16px 20px;margin-bottom:28px;">
                        <p style="margin:0;font-size:15px;color:#9a3412;line-height:1.6;">
                        If you believe this was a mistake or would like to appeal, please contact your administrator directly.
                      </p>
                    </div>
                    <p style="margin:0;font-size:14px;color:#64748b;">
                      This is an automated notification from RevnU.
                    </p>
                    """.formatted(fullname);

            String html = wrap("#ffedd5", icon, "Account Suspended", body);
            sendHtml(toEmail, "RevnU: Your account has been suspended", html);
            logger.info("Suspension email sent to {}", toEmail);
        } catch (Exception e) {
            logger.warn("Failed to send suspension email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendReactivationEmail(String toEmail, String fullname) {
        try {
            String icon = "<svg width='32' height='32' viewBox='0 0 24 24' fill='none' stroke='#059669' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M22 11.08V12a10 10 0 1 1-5.93-9.14'/><polyline points='22 4 12 14.01 9 11.01'/></svg>";
            String body = """
                    <p style="margin:0 0 16px;font-size:16px;color:#334155;line-height:1.6;">
                      Hi <strong>%s</strong>,
                    </p>
                    <p style="margin:0 0 24px;font-size:16px;color:#334155;line-height:1.6;">
                      Great news! Your <strong style="color:#0f172a;">RevnU</strong> account has been <strong style="color:#059669;">reactivated</strong>.
                      You can now log in and resume managing your restaurant.
                    </p>
                    <div style="background-color:#f0fdf4;border-left:4px solid #10b981;border-radius:0 8px 8px 0;padding:16px 20px;margin-bottom:28px;">
                      <p style="margin:0;font-size:15px;color:#065f46;line-height:1.6;">
                        Your data and settings are exactly as you left them. Welcome back!
                      </p>
                    </div>
                    <p style="margin:0;font-size:14px;color:#64748b;">
                      This is an automated notification from RevnU.
                    </p>
                    """.formatted(fullname);

            String html = wrap("#d1fae5", icon, "Account Reactivated", body);
            sendHtml(toEmail, "RevnU: Your account has been reactivated", html);
            logger.info("Reactivation email sent to {}", toEmail);
        } catch (Exception e) {
            logger.warn("Failed to send reactivation email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendOtpEmail(String toEmail, String fullname, String otp) {
        try {
            String icon = "<svg width='32' height='32' viewBox='0 0 24 24' fill='none' stroke='#6366f1' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><rect x='3' y='11' width='18' height='11' rx='2' ry='2'/><path d='M7 11V7a5 5 0 0 1 10 0v4'/></svg>";
            String body = """
                    <p style="margin:0 0 16px;font-size:16px;color:#334155;line-height:1.6;">
                      Hi <strong>%s</strong>,
                    </p>
                    <p style="margin:0 0 24px;font-size:16px;color:#334155;line-height:1.6;">
                      We received a request to reset your <strong style="color:#0f172a;">RevnU</strong> password.
                      Use the OTP below to proceed. It expires in <strong>10 minutes</strong>.
                    </p>
                    <div style="text-align:center;margin-bottom:28px;">
                      <div style="display:inline-block;background-color:#eef2ff;border:2px dashed #818cf8;border-radius:12px;padding:20px 40px;">
                        <span style="font-size:36px;font-weight:800;letter-spacing:10px;color:#4338ca;">%s</span>
                      </div>
                    </div>
                    <p style="margin:0 0 12px;font-size:14px;color:#64748b;">
                      If you did not request a password reset, you can safely ignore this email.
                      Your password will not change.
                    </p>
                    """.formatted(fullname, otp);

            String html = wrap("#eef2ff", icon, "Password Reset OTP", body);
            sendHtml(toEmail, "RevnU: Your Password Reset OTP", html);
            logger.info("OTP email sent to {}", toEmail);
        } catch (Exception e) {
            logger.warn("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendEodReport(String ownerEmail, String restaurantName, DailySummary summary) {
        try {
            String icon = "<svg width='32' height='32' viewBox='0 0 24 24' fill='none' stroke='#6366f1' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><line x1='18' y1='20' x2='18' y2='10'/><line x1='12' y1='20' x2='12' y2='4'/><line x1='6' y1='20' x2='6' y2='14'/></svg>";

            String netColor = summary.getNetProfit().compareTo(java.math.BigDecimal.ZERO) >= 0 ? "#059669" : "#dc2626";

            String body = """
                    <p style="margin:0 0 24px;font-size:16px;color:#334155;line-height:1.6;">
                      Here is your end-of-day summary for <strong style="color:#0f172a;">%s</strong>
                      on <strong>%s</strong>. All records have been locked.
                    </p>

                    <table width="100%%" cellpadding="16" cellspacing="0" style="background-color:#ffffff;border-radius:8px;border:1px solid #cbd5e1;margin-bottom:24px;">
                      <tr>
                        <td style="font-size:14px;color:#475569;border-bottom:1px solid #e2e8f0;">Total Sales</td>
                        <td style="font-size:15px;color:#0f172a;font-weight:600;text-align:right;border-bottom:1px solid #e2e8f0;">&#8369;%s</td>
                      </tr>
                      <tr>
                        <td style="font-size:14px;color:#475569;border-bottom:1px solid #e2e8f0;">Total Expenses</td>
                        <td style="font-size:15px;color:#0f172a;font-weight:600;text-align:right;border-bottom:1px solid #e2e8f0;">&#8369;%s</td>
                      </tr>
                      <tr>
                        <td style="font-size:14px;color:#475569;border-bottom:1px solid #e2e8f0;">Salaries Paid</td>
                        <td style="font-size:15px;color:#0f172a;font-weight:600;text-align:right;border-bottom:1px solid #e2e8f0;">&#8369;%s</td>
                      </tr>
                      <tr>
                        <td style="font-size:15px;color:#0f172a;font-weight:700;padding-top:20px;">Net Profit</td>
                        <td style="font-size:18px;font-weight:800;text-align:right;color:%s;padding-top:20px;">&#8369;%s</td>
                      </tr>
                    </table>

                    <div style="background-color:#f8fafc;border-radius:8px;padding:16px 20px;">
                      <p style="margin:0;font-size:14px;color:#475569;line-height:1.5;">
                        &#128274;&nbsp; These records are now <strong>locked</strong> in RevnU and cannot be edited.
                        View the full archived report in your dashboard.
                      </p>
                    </div>
                    """.formatted(
                    restaurantName,
                    summary.getReportDate(),
                    summary.getTotalSales(),
                    summary.getTotalExpenses(),
                    summary.getTotalSalaries(),
                    netColor,
                    summary.getNetProfit()
            );

            String html = wrap("#e0e7ff", icon, "End of Day Report", body);
            sendHtml(ownerEmail, "RevnU: End of Day Report for " + summary.getReportDate(), html);
            logger.info("EOD report email sent to {}", ownerEmail);
        } catch (Exception e) {
            logger.warn("Failed to send EOD report email to {}: {}", ownerEmail, e.getMessage());
        }
    }
}
