import { useState } from "react";
import { authService } from "../services/authService";
import { useAuth } from "../context/AuthContext";

export const useLogin = () => {
  const [form, setForm] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const { login } = useAuth();
  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e, onSuccessRedirect) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess("");

    try {
      const data = await authService.login(form);
      login(data);
      setSuccess("Login successful — redirecting…");

      if (onSuccessRedirect) {
        setTimeout(() => onSuccessRedirect(data), 600);
      }
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          "Login failed. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  };

  return { form, loading, error, success, handleChange, handleSubmit };
};
