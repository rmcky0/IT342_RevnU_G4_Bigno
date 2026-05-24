import { useState, useEffect } from "react";
import { useNavigate, useSearchParams, Link } from "react-router-dom";
import { authService } from "../services/authService";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../../../shared/components/Toast";
import revnuLogo from "../../../assets/revnu_logo.svg";

export const LinkGoogle = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { login } = useAuth();
  const { showToast } = useToast();

  const email = decodeURIComponent(searchParams.get("email") ?? "");
  const googleId = decodeURIComponent(searchParams.get("googleId") ?? "");

  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!email || !googleId) {
      navigate("/login", { replace: true });
    }
  }, [email, googleId, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const data = await authService.linkGoogle({ email, password, googleId });
      login(data);
      showToast("success", "Google linked — redirecting…");
      setTimeout(() => {
        if (data.role === "ADMIN") {
          navigate("/admin", { replace: true });
        } else if (data.hasRestaurant) {
          navigate("/dashboard", { replace: true });
        } else {
          navigate("/setup-restaurant", { replace: true });
        }
      }, 600);
    } catch (err) {
      showToast(
        "error",
        err.response?.data?.message ||
          err.response?.data ||
          "Failed to link account. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full flex flex-col">
      {/* ── Header Section ── */}
      <div className="text-center mb-8">
        <div className="w-16 h-16 flex items-center justify-center bg-white rounded-2xl shadow-[0_4px_20px_rgba(0,0,0,0.05)] border border-gray-100 p-3 mx-auto mb-6 transition-transform hover:scale-105 duration-300">
          <img
            src={revnuLogo}
            alt="RevnU Logo"
            className="w-full h-full object-contain"
          />
        </div>
        <h2 className="text-3xl font-extrabold text-gray-900 mb-3 tracking-tight">
          Link Google Account
        </h2>
        <p className="text-sm text-gray-500 leading-relaxed max-w-sm mx-auto">
          A RevnU account already exists for{" "}
          <span className="font-semibold text-gray-700">{email}</span>. Enter
          your password to confirm and link Google.
        </p>
      </div>

      {/* ── Form Card ── */}
      <div className="bg-white px-6 py-8 sm:p-10 shadow-[0_8px_30px_rgb(0,0,0,0.04)] border border-gray-100/80 rounded-3xl w-full">
        <form onSubmit={handleSubmit} className="space-y-5">
          {/* Email — read-only */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              Email
            </label>
            <input
              type="email"
              value={email}
              readOnly
              className="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm bg-gray-50 text-gray-500 cursor-not-allowed outline-none"
            />
          </div>

          {/* Password */}
          <div className="[&_input]:transition-all [&_input]:duration-200 [&_input:focus]:ring-2 [&_input:focus]:ring-[#7C6FF7]/20 [&_input:focus]:border-[#7C6FF7]">
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              Password
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter your current password"
              className="w-full px-4 py-3 border border-gray-300 rounded-xl text-sm placeholder-gray-400 focus:outline-none"
              required
              autoFocus
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3.5 bg-[#7C6FF7] hover:bg-[#6a5ee6] disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold rounded-xl text-sm transition-all shadow-lg shadow-[#7C6FF7]/25 active:scale-[0.98] mt-2"
          >
            {loading ? "Linking…" : "Confirm & Link Google Account"}
          </button>
        </form>

        <p className="text-center text-sm text-gray-500 mt-8 font-medium">
          Don't want to link?{" "}
          <Link
            to="/login"
            className="text-[#7C6FF7] font-semibold hover:underline"
          >
            Back to Sign In
          </Link>
        </p>
      </div>
    </div>
  );
};
