import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import revnuLogo from "../../../assets/revnu_logo.svg";

const RevnULogo = () => (
  <div className="w-20 h-20 flex items-center justify-center mb-6 mx-auto">
    <img
      src={revnuLogo}
      alt="RevnU Logo"
      className="w-full h-full object-contain"
    />
  </div>
);

export const AuthLayout = ({ children, title, subtitle }) => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex bg-white">
      {/* Left decorative panel */}
      <div className="hidden lg:flex w-[48%] m-5 rounded-3xl bg-[#7C6FF7] flex-col items-center justify-center p-12 relative overflow-hidden">
        <div className="absolute inset-0 rounded-3xl bg-gradient-to-br from-[#9185F5] to-[#6558D8]" />
        <div className="relative z-10 text-white max-w-xs text-left">
          <h1 className="text-4xl font-extrabold leading-snug">
            Take Control of
            <br />
            Your Restaurant's
            <br />
            <span className="text-blue-200">Revenue</span>
          </h1>
          <p className="mt-4 text-purple-100 text-sm leading-relaxed">
            Effortlessly track daily sales, manage expenses, and monitor staff
            payouts in one centralized digital dashboard.
          </p>
        </div>
        <div className="absolute -bottom-20 -left-20 w-72 h-72 rounded-full bg-white/5" />
        <div className="absolute -top-12 -right-12 w-56 h-56 rounded-full bg-white/5" />
      </div>

      {/* Right form panel */}
      <div className="flex-1 flex flex-col items-center justify-center px-8 pb-12">
        <div className="w-full max-w-md flex flex-col">
          <RevnULogo />
          <h2 className="text-3xl font-bold text-gray-900 mb-1 text-center">
            {title}
          </h2>
          <p className="text-sm text-gray-500 mb-8 text-center">{subtitle}</p>
          {children}
        </div>
      </div>
    </div>
  );
};
