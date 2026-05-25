package com.revnu.backend.features.reporting.email;

public class ReactivationEmailTemplate extends EmailTemplate {

    private final String fullname;

    public ReactivationEmailTemplate(String fullname) {
        this.fullname = fullname;
    }

    @Override
    protected String getSubject() {
        return "RevnU: Your account has been reactivated";
    }

    @Override
    protected String getAccentColor() {
        return "#d1fae5";
    }

    @Override
    protected String getIconSvg() {
        return "<svg width='32' height='32' viewBox='0 0 24 24' fill='none' stroke='#059669' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M22 11.08V12a10 10 0 1 1-5.93-9.14'/><polyline points='22 4 12 14.01 9 11.01'/></svg>";
    }

    @Override
    protected String getTitle() {
        return "Account Reactivated";
    }

    @Override
    protected String buildBody() {
        return """
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
    }
}
