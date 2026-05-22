import { Link } from "react-router-dom";
import { AuthLayout } from "../components/AuthLayout";
import { Notification } from "../components/Notification";
import { useForgotPassword } from "../hooks/useForgotPassword";

export const ForgotPassword = () => {
  const {
    step,
    email, setEmail,
    otp, setOtp,
    newPassword, setNewPassword,
    confirmPassword, setConfirmPassword,
    loading,
    cooldown,
    error,
    success,
    handleRequestOtp,
    handleVerifyOtp,
    handleResetPassword,
    handleResendOtp,
  } = useForgotPassword();

  // ── Step 1: Enter email ────────────────────────────────────────────────────
  if (step === "email") {
    return (
      <AuthLayout
        title="Forgot Password"
        subtitle="Enter your registered email and we'll send you a one-time code."
      >
        {error && <Notification type="error">{error}</Notification>}
        {success && <Notification type="success">{success}</Notification>}

        <form onSubmit={handleRequestOtp} className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Email Address
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="your@email.com"
              className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#2563EB] focus:border-transparent"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 bg-[#2563EB] hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold rounded-lg text-sm transition-colors"
          >
            {loading ? "Sending OTP…" : "Send OTP"}
          </button>
        </form>

        <p className="text-center text-sm text-gray-600 mt-6">
          <Link to="/login" className="text-gray-500 hover:text-[#2563EB]">
            ← Back to Login
          </Link>
        </p>
      </AuthLayout>
    );
  }

  // ── Step 2: Enter OTP ──────────────────────────────────────────────────────
  if (step === "otp") {
    return (
      <AuthLayout
        title="Enter OTP"
        subtitle={`We sent a 6-digit code to ${email}. It expires in 10 minutes.`}
      >
        {error && <Notification type="error">{error}</Notification>}
        {success && <Notification type="success">{success}</Notification>}

        <form onSubmit={handleVerifyOtp} className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              OTP Code
            </label>
            <input
              type="text"
              inputMode="numeric"
              maxLength={6}
              value={otp}
              onChange={(e) => setOtp(e.target.value.replace(/\D/g, ""))}
              placeholder="123456"
              className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm text-center tracking-widest font-bold placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#2563EB] focus:border-transparent"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading || otp.length !== 6}
            className="w-full py-3 bg-[#2563EB] hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold rounded-lg text-sm transition-colors"
          >
            {loading ? "Verifying…" : "Verify OTP"}
          </button>
        </form>

        <p className="text-center text-sm text-gray-500 mt-5">
          Didn't receive the code?{" "}
          <button
            onClick={handleResendOtp}
            disabled={loading || cooldown > 0}
            className="text-[#2563EB] font-semibold hover:underline disabled:opacity-50"
          >
            {cooldown > 0 ? `Resend in ${cooldown}s` : "Resend OTP"}
          </button>
        </p>

        <p className="text-center text-sm text-gray-600 mt-3">
          <Link to="/login" className="text-gray-500 hover:text-[#2563EB]">
            ← Back to Login
          </Link>
        </p>
      </AuthLayout>
    );
  }

  // ── Step 3: Set new password ───────────────────────────────────────────────
  if (step === "new-password") {
    return (
      <AuthLayout
        title="Set New Password"
        subtitle="OTP verified. Enter your new password below."
      >
        {error && <Notification type="error">{error}</Notification>}

        <form onSubmit={handleResetPassword} className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              New Password
            </label>
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="At least 8 characters"
              className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#2563EB] focus:border-transparent"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Confirm New Password
            </label>
            <input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Re-enter new password"
              className="w-full px-4 py-2.5 border border-gray-300 rounded-lg text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#2563EB] focus:border-transparent"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 bg-[#2563EB] hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold rounded-lg text-sm transition-colors"
          >
            {loading ? "Resetting…" : "Reset Password"}
          </button>
        </form>
      </AuthLayout>
    );
  }

  // ── Step 4: Done ───────────────────────────────────────────────────────────
  return (
    <AuthLayout
      title="Password Reset"
      subtitle="Your password has been updated successfully."
    >
      {success && <Notification type="success">{success}</Notification>}
      <Link
        to="/login"
        className="block w-full text-center py-3 bg-[#2563EB] hover:bg-blue-700 text-white font-semibold rounded-lg text-sm transition-colors mt-4"
      >
        Back to Login
      </Link>
    </AuthLayout>
  );
};
