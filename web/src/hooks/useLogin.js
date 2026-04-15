import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { authService } from "../services/authService";

export const useLogin = () => {
  const [form, setForm] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess("");

    try {
      const data = await authService.login(form);

      if (data.status === "PENDING") {
        authService.clearSession();
        setSuccess(data.message || "Your account is pending approval.");
        setTimeout(
          () =>
            navigate(
              "/pending-approval?name=" + encodeURIComponent(data.fullName),
            ),
          600,
        );
        return;
      }

      if (data.status === "APPROVED" && data.accessToken) {
        authService.saveSession(data);
        setSuccess(data.message || "Login successful.");
        setTimeout(() => navigate("/dashboard"), 600);
        return;
      }

      setError(data.message || "Unable to log in.");
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
