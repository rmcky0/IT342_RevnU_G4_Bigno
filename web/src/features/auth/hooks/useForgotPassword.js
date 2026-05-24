import { useState, useEffect, useRef } from "react";
import { authApi } from "../api/authApi";
import { useToast } from "../../../shared/components/Toast";

const RESEND_COOLDOWN = 60;

export const useForgotPassword = () => {
  const [step, setStep] = useState("email");
  const [email, setEmail] = useState("");
  const [otp, setOtp] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [cooldown, setCooldown] = useState(0);
  const cooldownRef = useRef(null);
  const { showToast } = useToast();

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

  const getErrMsg = (err, fallback) =>
    err?.response?.data?.error?.details ||
    err?.response?.data?.error?.message ||
    fallback;

  const handleRequestOtp = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await authApi.forgotPassword(email);
      showToast("success", "OTP sent! Check your email inbox.");
      startCooldown();
      setStep("otp");
    } catch (err) {
      showToast("error", getErrMsg(err, "Failed to send OTP. Please try again."));
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await authApi.verifyOtp(email, otp);
      setStep("new-password");
    } catch (err) {
      showToast("error", getErrMsg(err, "Incorrect or expired OTP."));
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();

    if (newPassword !== confirmPassword) {
      showToast("error", "Passwords do not match.");
      return;
    }

    setLoading(true);
    try {
      await authApi.resetPassword(email, otp, newPassword);
      showToast("success", "Password reset successfully! You can now log in.");
      setStep("done");
    } catch (err) {
      showToast("error", getErrMsg(err, "Failed to reset password. Please try again."));
    } finally {
      setLoading(false);
    }
  };

  const handleResendOtp = async () => {
    setLoading(true);
    try {
      await authApi.forgotPassword(email);
      showToast("success", "A new OTP has been sent to your email.");
      startCooldown();
    } catch (err) {
      showToast("error", getErrMsg(err, "Failed to resend OTP."));
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
    handleRequestOtp,
    handleVerifyOtp,
    handleResetPassword,
    handleResendOtp,
  };
};
