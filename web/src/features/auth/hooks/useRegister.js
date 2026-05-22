import { useState } from "react";
import { authService } from "../services/authService";
import { useAuth } from "../context/AuthContext";

export const useRegister = () => {
  const [form, setForm] = useState({
    fullname: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const { login } = useAuth();

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e, onSuccessRedirect) => {
    e.preventDefault();

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);
    setError("");
    setSuccess("");

    try {
      const data = await authService.register(form);

      if (data.token || data.accessToken) {
        login(data);

        setSuccess("Account created! Let's set up your restaurant...");
        if (onSuccessRedirect) {
          setTimeout(() => onSuccessRedirect("/setup-restaurant"), 1000);
        }
      } else {
        setSuccess("Account created successfully. Please log in.");
        if (onSuccessRedirect) {
          setTimeout(() => onSuccessRedirect("/login"), 1000);
        }
      }
    } catch (err) {
      setError(
        err.response?.data?.error?.details ||
          err.response?.data?.error?.message ||
          "Registration failed. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  };

  return { form, loading, error, success, handleChange, handleSubmit };
};
