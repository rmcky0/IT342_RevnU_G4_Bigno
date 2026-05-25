import { useNavigate } from "react-router-dom";
import { ArrowRight, ChevronRight } from "lucide-react";
import revnuLogo from "../../../../public/revnu.svg";
import { PolicyModal } from "../components/PolicyModal";
import { useState } from "react";

export const Welcome = () => {
  const navigate = useNavigate();
  const [modal, setModal] = useState(null);
  return (
    <div className="flex flex-col items-center w-full mx-auto">
      {/* Logo */}
      <div className="w-16 h-16 flex items-center justify-center bg-white rounded-2xl shadow-[0_4px_24px_rgba(124,111,247,0.15)] border border-gray-100 p-3 mb-8 transition-transform hover:scale-105 duration-300">
        <img
          src={revnuLogo}
          alt="RevnU Logo"
          className="w-full h-full object-contain"
        />
      </div>

      <h2 className="text-4xl font-black text-gray-900 tracking-tight text-center leading-tight">
        Welcome to <br />
        <span className="text-[#7C6FF7]">RevnU</span>
      </h2>
      <p className="mt-3 text-base text-gray-500 text-center leading-relaxed max-w-xs">
        Your all-in-one restaurant revenue management platform.
      </p>

      {/* CTAs */}
      <div className="mt-10 w-full space-y-3">
        <button
          onClick={() => navigate("/register")}
          className="w-full flex items-center justify-center gap-2 bg-[#7C6FF7] hover:bg-[#6a5ee6] text-white font-semibold text-sm py-3.5 rounded-2xl shadow-lg shadow-[#7C6FF7]/30 hover:shadow-[#7C6FF7]/40 transition-all duration-200 active:scale-[0.98]"
        >
          Get Started — It's Free
          <ArrowRight className="w-4 h-4" />
        </button>

        <button
          onClick={() => navigate("/login")}
          className="w-full flex items-center justify-center gap-2 bg-white hover:bg-gray-50 text-gray-800 font-semibold text-sm py-3.5 rounded-2xl border border-gray-200 shadow-sm hover:shadow-md transition-all duration-200 active:scale-[0.98]"
        >
          Sign In to Your Account
          <ChevronRight className="w-4 h-4 text-gray-400" />
        </button>
      </div>

      {/* Mobile feature pills */}
      <div className="lg:hidden mt-10 flex flex-wrap justify-center gap-2">
        {["Sales Tracking", "Expense Logs", "Staff Payroll", "EOD Reports"].map(
          (category) => (
            <span
              key={category}
              className="px-3 py-1 bg-[#7C6FF7]/8 text-[#7C6FF7] text-xs font-semibold rounded-full border border-[#7C6FF7]/15"
            >
              {category}
            </span>
          ),
        )}
      </div>

      {/* Trust */}
      <div className="mt-10 flex items-center gap-3">
        <div className="flex -space-x-2.5">
          {["bg-indigo-300", "bg-indigo-400", "bg-purple-300"].map((c, i) => (
            <div
              key={i}
              className={`w-8 h-8 rounded-full border-2 border-white ${c}`}
            />
          ))}
        </div>
        <p className="text-xs text-gray-500 font-medium">
          Trusted by{" "}
          <span className="text-gray-800 font-semibold">
            1,000+ restaurateurs
          </span>
        </p>
      </div>

      <p className="mt-8 text-xs text-gray-400 text-center">
        By continuing, you agree to our{" "}
        <span
          className="text-[#7C6FF7] hover:underline cursor-pointer font-medium"
          onClick={() => setModal("terms")}
        >
          Terms
        </span>
        {" & "}
        <span
          className="text-[#7C6FF7] hover:underline cursor-pointer font-medium"
          onClick={() => setModal("privacy")}
        >
          Privacy Policy
        </span>
      </p>
      {modal && <PolicyModal type={modal} onClose={() => setModal(null)} />}
    </div>
  );
};
