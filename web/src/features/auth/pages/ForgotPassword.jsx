import { Link } from "react-router-dom";
import { useForgotPassword } from "../hooks/useForgotPassword";
import revnuLogo from "../../../../public/revnu.svg";

const FormShell = ({ title, subtitle, children }) => (
  <div className="w-full flex flex-col">
    <div className="text-center mb-8">
      <div className="w-16 h-16 flex items-center justify-center bg-white rounded-2xl shadow-[0_4px_20px_rgba(0,0,0,0.05)] border border-gray-100 p-3 mx-auto mb-6 transition-transform hover:scale-105 duration-300">
        <img
          src={revnuLogo}
          alt="RevnU Logo"
          className="w-full h-full object-contain"
        />
      </div>
      <h2 className="text-3xl font-extrabold text-gray-900 mb-2 tracking-tight">
        {title}
      </h2>
      <p className="text-base text-gray-500 max-w-sm mx-auto">{subtitle}</p>
    </div>
    <div className="bg-white px-6 py-8 sm:p-10 shadow-[0_8px_30px_rgb(0,0,0,0.04)] border border-gray-100/80 rounded-3xl w-full">
      {children}
    </div>
  </div>
);

export const ForgotPassword = () => {
  const {
    step,
    email,
    setEmail,
    otp,
    setOtp,
    newPassword,
    setNewPassword,
    confirmPassword,
    setConfirmPassword,
    loading,
    cooldown,
    handleRequestOtp,
    handleVerifyOtp,
    handleResetPassword,
    handleResendOtp,
  } = useForgotPassword();

  if (step === "email") {
    return (
      <FormShell
        title="Forgot Password"
        subtitle="Enter your registered email and we'll send you a one-time code."
      >
        <form onSubmit={handleRequestOtp} className="space-y-5">
          <div className="[&_input]:transition-all [&_input]:duration-200 [&_input:focus]:ring-2 [&_input:focus]:ring-[#7C6FF7]/20 [&_input:focus]:border-[#7C6FF7]">
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              Email Address
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="owner@restaurant.com"
              className="w-full px-4 py-3 border border-gray-300 rounded-xl text-sm placeholder-gray-400 focus:outline-none"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3.5 bg-[#7C6FF7] hover:bg-[#6a5ee6] disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold rounded-xl text-sm transition-all shadow-lg shadow-[#7C6FF7]/25 active:scale-[0.98] mt-2"
          >
            {loading ? "Sending OTP…" : "Send OTP"}
          </button>
        </form>
      </FormShell>
    );
  }

  if (step === "otp") {
    return (
      <FormShell
        title="Enter OTP"
        subtitle={
          <>
            We sent a 6-digit code to{" "}
            <span className="font-semibold text-gray-700">{email}</span>. It
            expires in 10 minutes.
          </>
        }
      >
        <form onSubmit={handleVerifyOtp} className="space-y-5">
          <div className="[&_input]:transition-all [&_input]:duration-200 [&_input:focus]:ring-2 [&_input:focus]:ring-[#7C6FF7]/20 [&_input:focus]:border-[#7C6FF7]">
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              OTP Code
            </label>
            <input
              type="text"
              inputMode="numeric"
              maxLength={6}
              value={otp}
              onChange={(e) => setOtp(e.target.value.replace(/\D/g, ""))}
              placeholder="123456"
              className="w-full px-4 py-3 border border-gray-300 rounded-xl text-sm text-center tracking-widest font-bold placeholder-gray-300 focus:outline-none"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading || otp.length !== 6}
            className="w-full py-3.5 bg-[#7C6FF7] hover:bg-[#6a5ee6] disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold rounded-xl text-sm transition-all shadow-lg shadow-[#7C6FF7]/25 active:scale-[0.98] mt-2"
          >
            {loading ? "Verifying…" : "Verify OTP"}
          </button>
        </form>

        <p className="text-center text-sm text-gray-500 mt-6">
          Didn't receive the code?{" "}
          <button
            onClick={handleResendOtp}
            disabled={loading || cooldown > 0}
            className="text-[#7C6FF7] font-semibold hover:underline disabled:opacity-50"
          >
            {cooldown > 0 ? `Resend in ${cooldown}s` : "Resend OTP"}
          </button>
        </p>

        <p className="text-center text-sm text-gray-600 mt-4 font-medium">
          <Link
            to="/login"
            className="text-gray-400 hover:text-[#7C6FF7] transition-colors"
          >
            ← Back to Login
          </Link>
        </p>
      </FormShell>
    );
  }

  if (step === "new-password") {
    return (
      <FormShell
        title="Set New Password"
        subtitle="OTP verified. Enter your new password below."
      >
        <form onSubmit={handleResetPassword} className="space-y-5">
          <div className="[&_input]:transition-all [&_input]:duration-200 [&_input:focus]:ring-2 [&_input:focus]:ring-[#7C6FF7]/20 [&_input:focus]:border-[#7C6FF7]">
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              New Password
            </label>
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="At least 8 characters"
              className="w-full px-4 py-3 border border-gray-300 rounded-xl text-sm placeholder-gray-400 focus:outline-none"
              required
            />
          </div>

          <div className="[&_input]:transition-all [&_input]:duration-200 [&_input:focus]:ring-2 [&_input:focus]:ring-[#7C6FF7]/20 [&_input:focus]:border-[#7C6FF7]">
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              Confirm New Password
            </label>
            <input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Re-enter new password"
              className="w-full px-4 py-3 border border-gray-300 rounded-xl text-sm placeholder-gray-400 focus:outline-none"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3.5 bg-[#7C6FF7] hover:bg-[#6a5ee6] disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold rounded-xl text-sm transition-all shadow-lg shadow-[#7C6FF7]/25 active:scale-[0.98] mt-2"
          >
            {loading ? "Resetting…" : "Reset Password"}
          </button>
        </form>
      </FormShell>
    );
  }

  // ── Step 4: Done ───────────────────────────────────────────────────────────
  return (
    <FormShell
      title="Password Reset"
      subtitle="Your password has been updated successfully."
    >
      <Link
        to="/login"
        className="block w-full text-center py-3.5 bg-[#7C6FF7] hover:bg-[#6a5ee6] text-white font-semibold rounded-xl text-sm transition-all shadow-lg shadow-[#7C6FF7]/25 active:scale-[0.98] mt-4"
      >
        Back to Login
      </Link>
    </FormShell>
  );
};
