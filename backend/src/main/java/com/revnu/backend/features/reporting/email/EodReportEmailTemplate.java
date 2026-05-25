package com.revnu.backend.features.reporting.email;

import com.revnu.backend.features.reporting.model.DailySummary;

public class EodReportEmailTemplate extends EmailTemplate {

    private final String ownerEmail;
    private final String restaurantName;
    private final DailySummary summary;

    public EodReportEmailTemplate(String ownerEmail, String restaurantName, DailySummary summary) {
        this.ownerEmail = ownerEmail;
        this.restaurantName = restaurantName;
        this.summary = summary;
    }

    public String getRecipient() {
        return ownerEmail;
    }

    @Override
    protected String getSubject() {
        return "RevnU: End of Day Report for " + summary.getReportDate();
    }

    @Override
    protected String getAccentColor() {
        return "#e0e7ff";
    }

    @Override
    protected String getIconSvg() {
        return "<svg width='32' height='32' viewBox='0 0 24 24' fill='none' stroke='#6366f1' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><line x1='18' y1='20' x2='18' y2='10'/><line x1='12' y1='20' x2='12' y2='4'/><line x1='6' y1='20' x2='6' y2='14'/></svg>";
    }

    @Override
    protected String getTitle() {
        return "End of Day Report";
    }

    @Override
    protected String buildBody() {
        String netColor = summary.getNetProfit().compareTo(java.math.BigDecimal.ZERO) >= 0
                ? "#059669"
                : "#dc2626";

        return """
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
                summary.getNetProfit());
    }
}
