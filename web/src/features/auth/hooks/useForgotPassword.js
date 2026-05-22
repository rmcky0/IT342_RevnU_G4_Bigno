import { useState, useEffect, useRef } from "react";
import { authApi } from "../api/authApi";

const RESEND_COOLDOWN = 60;

export const useForgotPassword = () => {
  const [step, setStep] = useState("email"); // "email" | "otp" | "new-password" | "done"
  const [email, setEmail] = useState("");
  const [otp, setOtp] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [cooldown, setCooldown] = useState(0);
  const cooldownRef = useRef(null);

  useEffect(() => {
    return () => clearInterval(cooldownRef.current);
  }, []);

  const startCooldown = () => {
    setCooldown(RESEND_COOLDOWN);
    cooldownRef.current = setInterval(() => {
      setCooldown((prev) => {
        if (prev <= 1) {
          clearInterval(cooldownRef.current);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  };

  const clearMessages = () => { setError(""); setSuccess(""); };

  const handleRequestOtp = async (e) => {
    e.preventDefault();
    clearMessages();
    setLoading(true);
    try {
      await authApi.forgotPassword(email);
      setSuccess("OTP sent! Check your email inbox.");
      startCooldown();
      setStep("otp");
    } catch (err) {
      setError(
        err?.response?.data?.error?.details ||
          err?.response?.data?.error?.message ||
          "Failed to send OTP. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    clearMessages();
    setLoading(true);
    try {
      await authApi.verifyOtp(email, otp);
      setSuccess("");
      setStep("new-password");
    } catch (err) {
      setError(
        err?.response?.data?.error?.details ||
          err?.response?.data?.error?.message ||
          "Incorrect or expired OTP."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    clearMessages();

    if (newPassword !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }
    if (newPassword.length < 8) {
      setError("Password must be at least 8 characters.");
      return;
    }

    setLoading(true);
    try {
      await authApi.resetPassword(email, otp, newPassword);
      setSuccess("Password reset successfully! You can now log in.");
      setStep("done");
    } catch (err) {
      setError(
        err?.response?.data?.error?.details ||
          err?.response?.data?.error?.message ||
          "Failed to reset password. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleResendOtp = async () => {
    clearMessages();
    setLoading(true);
    try {
      await authApi.forgotPassword(email);
      setSuccess("A new OTP has been sent to your email.");
      startCooldown();
    } catch (err) {
      setError(
        err?.response?.data?.error?.details ||
          err?.response?.data?.error?.message ||
          "Failed to resend OTP."
      );
    } finally {
      setLoading(false);
    }
  };

  return {
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
  };
};
