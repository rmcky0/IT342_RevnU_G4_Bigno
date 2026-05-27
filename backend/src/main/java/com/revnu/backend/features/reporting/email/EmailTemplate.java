package com.revnu.backend.features.reporting.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revnu.backend.shared.mail.MailProvider;

public abstract class EmailTemplate {

    private static final Logger logger = LoggerFactory.getLogger(EmailTemplate.class);

    protected abstract String getSubject();

    protected abstract String getAccentColor();

    protected abstract String getIconSvg();

    protected abstract String getTitle();

    protected abstract String buildBody();

    public final void send(String to, MailProvider mailProvider, String fromAddress) {
        try {
            String html = wrap(getAccentColor(), getIconSvg(), getTitle(), buildBody());
            mailProvider.send(to, fromAddress, getSubject(), html);
            logger.info("{} sent to {}", getClass().getSimpleName(), to);
        } catch (Exception e) {
            logger.warn("Failed to send {} to {}: {}", getClass().getSimpleName(), to, e.getMessage());
        }
    }

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
}
