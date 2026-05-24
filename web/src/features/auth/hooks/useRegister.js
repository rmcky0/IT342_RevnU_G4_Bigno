import { useState } from "react";
import { authService } from "../services/authService";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../../../shared/components/Toast";

export const useRegister = () => {
  const [form, setForm] = useState({
    fullname: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const { showToast } = useToast();

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e, onSuccessRedirect) => {
    e.preventDefault();

    if (form.password !== form.confirmPassword) {
      showToast("error", "Passwords do not match.");
      return;
    }

    setLoading(true);

    try {
      const data = await authService.register(form);

      if (data.token || data.accessToken) {
        login(data);
        if (onSuccessRedirect) {
          setTimeout(() => onSuccessRedirect("/setup-restaurant"), 300);
        }
      } else {
        showToast("success", "Account created successfully. Please log in.");
        if (onSuccessRedirect) {
          setTimeout(() => onSuccessRedirect("/login"), 1200);
        }
      }
    } catch (err) {
      showToast(
        "error",
        err.response?.data?.error?.details ||
          err.response?.data?.error?.message ||
          "Registration failed. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  };

  return { form, loading, handleChange, handleSubmit };
};
