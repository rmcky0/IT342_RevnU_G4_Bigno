import { useLocation, useNavigate, Outlet } from "react-router-dom";
import {
  TrendingUp,
  DollarSign,
  Users,
  BarChart3,
  ArrowLeft,
} from "lucide-react";

const Feature = ({ icon: Icon, title, desc }) => (
  <div className="flex items-start gap-4 group">
    <div className="flex-shrink-0 w-10 h-10 rounded-2xl bg-white/10 border border-white/20 flex items-center justify-center group-hover:bg-white/20 transition-colors backdrop-blur-sm">
      <Icon className="w-5 h-5 text-white" />
    </div>
    <div>
      <p className="text-white font-semibold text-sm leading-tight">{title}</p>
      <p className="text-indigo-200/80 text-xs mt-0.5 leading-relaxed">
        {desc}
      </p>
    </div>
  </div>
);

const Stat = ({ value, label }) => (
  <div className="text-center">
    <p className="text-3xl font-black text-white tracking-tight">{value}</p>
    <p className="text-indigo-200/80 text-xs font-medium mt-0.5">{label}</p>
  </div>
);

const WelcomeLeft = () => (
  <div className="flex flex-col h-full justify-between">
    <span className="inline-flex items-center gap-1.5 px-4 py-1.5 rounded-full bg-white/10 border border-white/20 text-white text-xs font-semibold tracking-wide uppercase backdrop-blur-md w-fit">
      ✨ RevnU Platform
    </span>

    <div>
      <h1 className="text-5xl xl:text-[3.25rem] font-black text-white leading-[1.1] tracking-tight">
        Take Control <br />
        of Your Restaurant's <br />
        <span className="text-transparent bg-clip-text bg-gradient-to-r from-[#D8D3FE] to-white">
          Revenue
        </span>
      </h1>
      <p className="mt-5 text-base text-indigo-100/90 font-medium leading-relaxed max-w-sm">
        Effortlessly track daily sales, manage expenses, and monitor staff
        payouts in one centralized digital dashboard.
      </p>
      <div className="mt-8 space-y-4">
        <Feature
          icon={TrendingUp}
          title="Daily Sales Tracking"
          desc="Log and monitor every transaction in real time"
        />
        <Feature
          icon={DollarSign}
          title="Expense Management"
          desc="Categorize and review all outgoing costs"
        />
        <Feature
          icon={Users}
          title="Staff Payroll"
          desc="Record and track salary payouts with full history"
        />
        <Feature
          icon={BarChart3}
          title="End-of-Day Reports"
          desc="Auto-generate EOD summaries with profit insights"
        />
      </div>
    </div>

    <div className="flex items-center justify-between pt-8 border-t border-white/15">
      <Stat value="1,000+" label="Restaurateurs" />
      <div className="w-px h-8 bg-white/20" />
      <Stat value="99.9%" label="Uptime" />
      <div className="w-px h-8 bg-white/20" />
      <Stat value="24/7" label="Support" />
    </div>
  </div>
);

const AuthLeft = () => (
  <div className="flex flex-col h-full justify-between">
    <span className="inline-flex items-center px-4 py-1.5 rounded-full bg-white/10 border border-white/20 text-white text-xs font-semibold tracking-wide uppercase backdrop-blur-md w-fit">
      ✨ RevnU Platform
    </span>

    <div>
      <h1 className="text-5xl xl:text-[3.5rem] font-black text-white leading-[1.1] tracking-tight">
        Take Control <br />
        of Your Restaurant's <br />
        <span className="text-transparent bg-clip-text bg-gradient-to-r from-[#D8D3FE] to-white">
          Revenue
        </span>
      </h1>
      <p className="mt-6 text-lg text-indigo-100/90 font-medium leading-relaxed max-w-md">
        Effortlessly track daily sales, manage expenses, and monitor staff
        payouts in one centralized digital dashboard.
      </p>
    </div>

    <div className="flex items-center gap-4 text-white text-sm font-medium">
      <div className="flex -space-x-3">
        <div className="w-10 h-10 rounded-full border-2 border-[#7C6FF7] bg-indigo-300" />
        <div className="w-10 h-10 rounded-full border-2 border-[#7C6FF7] bg-indigo-400" />
        <div className="w-10 h-10 rounded-full border-2 border-[#7C6FF7] bg-indigo-200" />
      </div>
      <p className="opacity-90">
        Trusted by 1000+ <br /> Restaurateurs
      </p>
    </div>
  </div>
);

export const AuthShell = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const isWelcome = location.pathname === "/";

  return (
    <div className="h-screen flex bg-gray-50/50 overflow-hidden">
      {/* ── Left panel ─────────────────────────────────────────── */}
      <div className="hidden lg:flex lg:w-1/2 xl:w-[45%] p-4 lg:p-6 animate-left-panel-enter">
        <div className="w-full h-full rounded-[2.5rem] bg-[#7C6FF7] relative overflow-hidden p-12 xl:p-16 shadow-2xl shadow-[#7C6FF7]/20">
          {/* Backgrounds */}
          <div className="absolute inset-0 bg-gradient-to-br from-[#8d82f8] via-[#7C6FF7] to-[#5a4cdb]" />
          <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.06)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.06)_1px,transparent_1px)] bg-[size:40px_40px]" />
          <div className="absolute -top-[20%] -left-[10%] w-[70%] h-[70%] rounded-full bg-gradient-to-br from-white/20 to-transparent blur-3xl animate-[pulse_6s_ease-in-out_infinite]" />
          <div className="absolute -bottom-[20%] -right-[10%] w-[60%] h-[60%] rounded-full bg-gradient-to-tl from-white/10 to-transparent blur-2xl animate-[pulse_8s_ease-in-out_infinite_reverse]" />
          <div className="absolute top-[25%] right-[10%] w-40 h-48 bg-white/10 backdrop-blur-md border border-white/20 rounded-3xl rotate-12 animate-[bounce_8s_infinite]" />
          <div className="absolute top-[35%] right-[25%] w-20 h-20 bg-white/5 backdrop-blur-sm border border-white/10 rounded-full -rotate-12 animate-[bounce_6s_infinite_reverse]" />

          <div
            key={isWelcome ? "welcome" : "auth"}
            className="relative z-10 h-full animate-panel-enter"
          >
            {isWelcome ? <WelcomeLeft /> : <AuthLeft />}
          </div>
        </div>
      </div>

      {/* ── Right panel ────────────────────────────────────────── */}
      <div className="flex-1 flex flex-col items-center justify-center px-6 sm:px-12 lg:px-16 py-12 relative overflow-y-auto">
        {!isWelcome && (
          <button
            onClick={() => navigate("/")}
            className="absolute top-8 left-6 sm:left-12 flex items-center gap-2 text-sm font-medium text-gray-400 hover:text-gray-900 transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            Back
          </button>
        )}

        <div
          key={location.pathname}
          className="w-full max-w-md animate-panel-enter"
        >
          <Outlet />
        </div>
      </div>
    </div>
  );
};
