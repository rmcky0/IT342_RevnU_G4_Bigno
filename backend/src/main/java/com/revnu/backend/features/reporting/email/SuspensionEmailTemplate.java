package com.revnu.backend.features.reporting.email;

public class SuspensionEmailTemplate extends EmailTemplate {

    private final String fullname;

    public SuspensionEmailTemplate(String fullname) {
        this.fullname = fullname;
    }

    @Override
    protected String getSubject() {
        return "RevnU: Your account has been suspended";
    }

    @Override
    protected String getAccentColor() {
        return "#ffedd5";
    }

    @Override
    protected String getIconSvg() {
        return "<svg width='32' height='32' viewBox='0 0 24 24' fill='none' stroke='#ea580c' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><rect x='3' y='11' width='18' height='11' rx='2' ry='2'/><path d='M7 11V7a5 5 0 0 1 10 0v4'/></svg>";
    }

    @Override
    protected String getTitle() {
        return "Account Suspended";
    }

    @Override
    protected String buildBody() {
        return """
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
    }
}
