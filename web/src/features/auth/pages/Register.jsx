import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useRegister } from "../hooks/useRegister";
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

const CheckIcon = ({ isValid }) => (
  <div
    className={`flex items-center justify-center w-3.5 h-3.5 rounded-full border transition-colors duration-300 ${isValid ? "bg-green-500 border-green-500 text-white" : "border-gray-300 bg-transparent"}`}
  >
    {isValid && (
      <svg
        className="w-2.5 h-2.5"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
        strokeWidth={3}
      >
        <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
      </svg>
    )}
  </div>
);

const API_BASE_URL =
  import.meta.env.VITE_API_URL || "http://localhost:8080/api/v1";

export const Register = () => {
  const { form, loading, handleChange, handleSubmit } = useRegister();
  const navigate = useNavigate();
  const [modal, setModal] = useState(null);

  // Dynamic password criteria checks
  const passwordRules = [
    {
      id: "length",
      label: "8+ characters",
      isValid: form.password.length >= 8,
    },
    { id: "upper", label: "1 uppercase", isValid: /[A-Z]/.test(form.password) },
    { id: "lower", label: "1 lowercase", isValid: /[a-z]/.test(form.password) },
    { id: "number", label: "1 number", isValid: /[0-9]/.test(form.password) },
    {
      id: "special",
      label: "1 special char",
      isValid: /[^A-Za-z0-9]/.test(form.password),
    },
  ];

  const isPasswordValid = passwordRules.every((rule) => rule.isValid);
  const passwordsMatch =
    form.password && form.password === form.confirmPassword;

  const onFormSubmit = (e) => {
    e.preventDefault();
    if (!isPasswordValid || !passwordsMatch) return;
    handleSubmit(e, (destination) => {
      navigate(destination);
    });
  };

  return (
    <div className="w-full flex flex-col">
      <div className="text-center mb-6">
        <div className="w-14 h-14 flex items-center justify-center bg-white rounded-2xl shadow-sm border border-gray-100 p-2.5 mx-auto mb-4 transition-transform hover:scale-105 duration-300">
          <img
            src={revnuLogo}
            alt="RevnU Logo"
            className="w-full h-full object-contain"
          />
        </div>
        <h2 className="text-2xl font-extrabold text-gray-900 mb-1.5 tracking-tight">
          Create Your Account
        </h2>
        <p className="text-sm text-gray-500">
          Join the digital transformation for your restaurant.
        </p>
      </div>

      <div className="bg-white px-6 py-6 sm:px-8 sm:py-8 shadow-[0_4px_24px_rgb(0,0,0,0.03)] border border-gray-100/80 rounded-[1.5rem] w-full">
        <button
          type="button"
          onClick={() => {
            sessionStorage.setItem("oauth2_source", "/register");
            window.location.href = `${API_BASE_URL}/oauth2/authorization/google`;
          }}
          className="w-full flex items-center justify-center gap-3 py-2.5 px-4 bg-white border border-gray-200 shadow-sm rounded-xl text-sm font-semibold text-gray-700 hover:bg-gray-50 hover:border-gray-300 transition-all mb-5 active:scale-[0.98]"
        >
          <GoogleIcon />
          Continue with Google
        </button>

        <div className="flex items-center gap-3 mb-5">
          <hr className="flex-1 border-gray-100" />
          <span className="text-[11px] text-gray-400 font-semibold uppercase tracking-widest">
            or register with email
          </span>
          <hr className="flex-1 border-gray-100" />
        </div>

        <form onSubmit={onFormSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              Full Name
            </label>
            <input
              type="text"
              name="fullname"
              value={form.fullname}
              onChange={handleChange}
              placeholder="e.g. Juan Dela Cruz"
              className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm placeholder-gray-400 focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#7C6FF7]/20 focus:border-[#7C6FF7] transition-all duration-200"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1.5">
              Email
            </label>
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="name@example.com"
              className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm placeholder-gray-400 focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#7C6FF7]/20 focus:border-[#7C6FF7] transition-all duration-200"
              required
            />
          </div>

          <div className="space-y-3">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1.5">
                  Password
                </label>
                <input
                  type="password"
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  placeholder="Min 8 chars"
                  className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm placeholder-gray-400 focus:bg-white focus:outline-none focus:ring-2 focus:ring-[#7C6FF7]/20 focus:border-[#7C6FF7] transition-all duration-200"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1.5">
                  Confirm
                </label>
                <input
                  type="password"
                  name="confirmPassword"
                  value={form.confirmPassword}
                  onChange={handleChange}
                  placeholder="Re-enter password"
                  className={`w-full px-4 py-2.5 bg-gray-50 border rounded-xl text-sm placeholder-gray-400 focus:bg-white focus:outline-none focus:ring-2 transition-all duration-200 ${form.confirmPassword && !passwordsMatch ? "border-red-300 focus:ring-red-100 focus:border-red-400" : "border-gray-200 focus:ring-[#7C6FF7]/20 focus:border-[#7C6FF7]"}`}
                  required
                />
              </div>
            </div>

            {/* Dynamic Check UI */}
            {form.password.length > 0 && (
              <div className="grid grid-cols-2 gap-y-2 gap-x-4 bg-gray-50 p-3 rounded-lg border border-gray-100">
                {passwordRules.map((rule) => (
                  <div key={rule.id} className="flex items-center gap-2">
                    <CheckIcon isValid={rule.isValid} />
                    <span
                      className={`text-[11px] font-medium transition-colors duration-300 ${rule.isValid ? "text-gray-700" : "text-gray-400"}`}
                    >
                      {rule.label}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>

          <button
            type="submit"
            disabled={loading || !isPasswordValid || !passwordsMatch}
            className="w-full py-3 bg-[#7C6FF7] hover:bg-[#6a5ee6] disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold rounded-xl text-sm transition-all shadow-md shadow-[#7C6FF7]/20 active:scale-[0.98] mt-2"
          >
            {loading ? "Creating account…" : "Create Account"}
          </button>

          <p className="text-center text-[11px] text-gray-400 mt-3 leading-relaxed">
            By registering, you agree to our{" "}
            <button
              type="button"
              onClick={() => setModal("terms")}
              className="underline hover:text-gray-600"
            >
              Terms of Service
            </button>{" "}
            and{" "}
            <button
              type="button"
              onClick={() => setModal("privacy")}
              className="underline hover:text-gray-600"
            >
              Privacy Policy
            </button>
            .
          </p>
        </form>

        <p className="text-center text-sm text-gray-500 mt-6">
          Already have an account?{" "}
          <Link
            to="/login"
            className="text-[#7C6FF7] font-semibold hover:underline"
          >
            Sign In here
          </Link>
        </p>
      </div>

      {modal && <PolicyModal type={modal} onClose={() => setModal(null)} />}
    </div>
  );
};
