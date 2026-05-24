import { useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { useLogin } from "../hooks/useLogin";
import { PolicyModal } from "../components/PolicyModal";
import revnuLogo from "../../../../public/revnu.svg";

const GoogleIcon = () => (
  <svg
    width="18"
    height="18"
    viewBox="0 0 18 18"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M17.64 9.2c0-.637-.057-1.251-.164-1.84H9v3.481h4.844c-.209 1.125-.843 2.078-1.796 2.717v2.258h2.908c1.702-1.567 2.684-3.874 2.684-6.615z"
      fill="#4285F4"
    />
    <path
      d="M9 18c2.43 0 4.467-.806 5.956-2.184l-2.908-2.258c-.806.54-1.837.86-3.048.86-2.344 0-4.328-1.584-5.036-3.711H.957v2.332C2.438 15.983 5.482 18 9 18z"
      fill="#34A853"
    />
    <path
      d="M3.964 10.707c-.18-.54-.282-1.117-.282-1.707s.102-1.167.282-1.707V4.961H.957C.347 6.175 0 7.55 0 9s.348 2.825.957 4.039l3.007-2.332z"
      fill="#FBBC05"
    />
    <path
      d="M9 3.58c1.321 0 2.508.454 3.44 1.345l2.582-2.58C13.463.891 11.426 0 9 0 5.482 0 2.438 2.017.957 4.961L3.964 7.293C4.672 5.166 6.656 3.58 9 3.58z"
      fill="#EA4335"
    />
  </svg>
);

const API_BASE_URL =
  import.meta.env.VITE_API_URL || "http://localhost:8080/api/v1";

export const Login = () => {
  const { form, loading, handleChange, handleSubmit } = useLogin();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [modal, setModal] = useState(null);

  const emailExistsInfo = searchParams.get("info") === "email_exists";
  const emailHint = searchParams.get("hint") ?? "";

  const onFormSubmit = (e) => {
    handleSubmit(e, (data) => {
      if (data.role === "ADMIN") navigate("/admin");
      else if (data.role === "RESTAURATEUR") {
        data.hasRestaurant
          ? navigate("/dashboard")
          : navigate("/setup-restaurant");
      } else navigate("/dashboard");
    });
  };

  return (
    <div className="w-full flex flex-col">
      <div className="text-center mb-8">
        <div className="w-14 h-14 flex items-center justify-center bg-white rounded-2xl shadow-sm border border-gray-100 p-2.5 mx-auto mb-5 transition-transform hover:scale-105 duration-300">
          <img
            src={revnuLogo}
            alt="RevnU Logo"
            className="w-full h-full object-contain"
          />
        </div>
        <h2 className="text-2xl font-extrabold text-gray-900 mb-1.5 tracking-tight">
          Welcome Back
        </h2>
        <p className="text-sm text-gray-500">
          Sign in to manage your restaurant's revenue.
        </p>
      </div>

      <div className="bg-white px-6 py-8 sm:px-10 sm:py-9 shadow-[0_4px_24px_rgb(0,0,0,0.03)] border border-gray-100/80 rounded-[1.5rem] w-full">
        {emailExistsInfo && (
          <div className="mb-4 px-4 py-3 bg-amber-50 border border-amber-200 rounded-xl text-[13px] text-amber-800 font-medium leading-snug">
            An account for{emailHint ? <> <span className="font-bold">{emailHint}</span></> : " this email"} already exists. Please sign in with your email and password below.
          </div>
        )}
        <button
          type="button"
          onClick={() => {
            sessionStorage.setItem("oauth2_source", "/login");
            window.location.href = `${API_BASE_URL}/oauth2/authorization/google`;
          }}
          className="w-full flex items-center justify-center gap-3 py-2.5 px-4 bg-white border border-gray-200 shadow-sm rounded-xl text-sm font-semibold text-gray-700 hover:bg-gray-50 hover:border-gray-300 transition-all mb-6 active:scale-[0.98]"
        >
          <GoogleIcon />
          Continue with Google
        </button>

        <div className="flex items-center gap-3 mb-6">
          <hr className="flex-1 border-gray-100" />
          <span className="text-[11px] text-gray-400 font-semibold uppercase tracking-widest">
            or sign in with email
          </span>
          <hr className="flex-1 border-gray-100" />
        </div>

        <form onSubmit={onFormSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              Email
            </label>
            <input
              type="text"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="owner@restaurant.com"
              className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm placeholder-gray-400 focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#7C6FF7]/20 focus:border-[#7C6FF7] transition-all duration-200"
              required
            />
          </div>

          <div>
            <div className="flex items-center justify-between mb-1.5">
              <label className="block text-sm font-medium text-gray-700">
                Password
              </label>
              <Link
                to="/forgot-password"
                className="text-xs font-medium text-[#7C6FF7] hover:text-[#6a5ee6]"
              >
                Forgot Password?
              </Link>
            </div>
            <input
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              placeholder="••••••••"
              className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm placeholder-gray-400 focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#7C6FF7]/20 focus:border-[#7C6FF7] transition-all duration-200"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 bg-[#7C6FF7] hover:bg-[#6a5ee6] disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold rounded-xl text-sm transition-all shadow-md shadow-[#7C6FF7]/20 active:scale-[0.98] mt-2"
          >
            {loading ? "Signing in…" : "Sign in"}
          </button>
        </form>

        <p className="text-center text-sm text-gray-500 mt-8">
          Don't have an account?{" "}
          <Link
            to="/register"
            className="text-[#7C6FF7] font-semibold hover:underline"
          >
            Register here
          </Link>
        </p>
      </div>

      {modal && <PolicyModal type={modal} onClose={() => setModal(null)} />}
    </div>
  );
};
