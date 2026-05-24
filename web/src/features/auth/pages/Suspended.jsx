import { Link } from "react-router-dom";
import revnuLogo from "../../../assets/revnu_logo.svg";

export const Suspended = () => (
  <div className="min-h-screen bg-gradient-to-br from-[#f4f5fa] to-indigo-50/30 flex items-center justify-center p-4">
    <div className="bg-white rounded-2xl shadow-[0_8px_40px_rgb(0,0,0,0.06)] border border-gray-100 p-10 max-w-md w-full text-center">
      {/* Logo */}
      <div className="flex items-center justify-center gap-2.5 mb-8">
        <img src={revnuLogo} alt="RevnU" className="w-7 h-7" />
        <span
          className="text-[22px] text-[#1e1b4b] tracking-wide"
          style={{ fontFamily: "'Bagel Fat One', system-ui" }}
        >
          RevnU
        </span>
      </div>

      {/* Icon */}
      <div className="w-16 h-16 bg-amber-50 border border-amber-100 rounded-2xl flex items-center justify-center mx-auto mb-6">
        <svg
          className="w-8 h-8 text-amber-500"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={1.5}
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M16.5 10.5V6.75a4.5 4.5 0 1 0-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 0 0 2.25-2.25v-6.75a2.25 2.25 0 0 0-2.25-2.25H6.75a2.25 2.25 0 0 0-2.25 2.25v6.75a2.25 2.25 0 0 0 2.25 2.25z"
          />
        </svg>
      </div>

      <h1 className="text-xl font-bold text-[#1e1b4b] mb-2">
        Account Suspended
      </h1>
      <p className="text-sm text-gray-500 leading-relaxed mb-8">
        Your RevnU account has been suspended by an administrator. If you
        believe this is a mistake, please contact support.
      </p>

      <Link
        to="/login"
        className="inline-flex items-center justify-center w-full py-2.5 px-6 bg-[#1e1b4b] text-white rounded-lg font-semibold text-sm hover:bg-indigo-900 transition-colors"
      >
        Back to Login
      </Link>
    </div>
  </div>
);
