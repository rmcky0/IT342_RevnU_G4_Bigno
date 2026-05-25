package com.revnu.backend.features.reporting.email;

public class WelcomeEmailTemplate extends EmailTemplate {

    private final String fullname;

    public WelcomeEmailTemplate(String fullname) {
        this.fullname = fullname;
    }

    @Override
    protected String getSubject() {
        return "Welcome to RevnU!";
    }

    @Override
    protected String getAccentColor() {
        return "#e0e7ff";
    }

    @Override
    protected String getIconSvg() {
        return "<svg width='32' height='32' viewBox='0 0 24 24' fill='none' stroke='#6366f1' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'/><circle cx='12' cy='7' r='4'/></svg>";
    }

    @Override
    protected String getTitle() {
        return "Welcome to RevnU!";
    }

    @Override
    protected String buildBody() {
        return """
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
    }
}
