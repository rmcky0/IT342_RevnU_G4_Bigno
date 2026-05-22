import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { authService } from "../services/authService";
import { useAuth } from "../context/AuthContext";

export const useLogin = () => {
  const [form, setForm] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const { login } = useAuth();
  const navigate = useNavigate();

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
      const detail =
        err.response?.data?.error?.details ||
        err.response?.data?.error?.message ||
        "";
      if (
        err.response?.status === 403 &&
        detail.toLowerCase().includes("suspended")
      ) {
        navigate("/suspended");
        return;
      }
      setError(detail || "Login failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return { form, loading, error, success, handleChange, handleSubmit };
};
