package com.revnu.backend.features.reporting.email;

public class OtpEmailTemplate extends EmailTemplate {

    private final String fullname;
    private final String otp;

    public OtpEmailTemplate(String fullname, String otp) {
        this.fullname = fullname;
        this.otp = otp;
    }

    @Override
    protected String getSubject() {
        return "RevnU: Your Password Reset OTP";
    }

    @Override
    protected String getAccentColor() {
        return "#eef2ff";
    }

    @Override
    protected String getIconSvg() {
        return "<svg width='32' height='32' viewBox='0 0 24 24' fill='none' stroke='#6366f1' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><rect x='3' y='11' width='18' height='11' rx='2' ry='2'/><path d='M7 11V7a5 5 0 0 1 10 0v4'/></svg>";
    }

    @Override
    protected String getTitle() {
        return "Password Reset OTP";
    }

    @Override
    protected String buildBody() {
        return """
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
    }
}
